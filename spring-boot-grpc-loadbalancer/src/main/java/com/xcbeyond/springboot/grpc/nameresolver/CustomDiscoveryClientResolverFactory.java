package com.xcbeyond.springboot.grpc.nameresolver;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;

import io.grpc.NameResolverProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor;
import org.springframework.context.event.EventListener;

import io.grpc.Attributes.Key;
import io.grpc.NameResolver;
import io.grpc.internal.GrpcUtil;

/**
 * A name resolver factory that will create a {@link CustomDiscoveryClientNameResolver} based on the target uri.
 */
// Do not add this to the NameResolverProvider service loader list
public class CustomDiscoveryClientResolverFactory extends NameResolverProvider {

    /**
     * The constant containing the scheme that will be used by this factory.
     */
    public static final String DISCOVERY_SCHEME = "discovery";
    /**
     * A key for the service name used to related {@link ServiceInstance}s from the {@link DiscoveryClient}.
     */
    public static final Key<String> DISCOVERY_SERVICE_NAME_KEY = Key.create("serviceName");
    /**
     * A key for the {@link ServiceInstance#getInstanceId() instance id}.
     */
    public static final Key<String> DISCOVERY_INSTANCE_ID_KEY = Key.create("instanceId");

    private final Set<CustomDiscoveryClientNameResolver> discoveryClientNameResolvers = ConcurrentHashMap.newKeySet();
    private final HeartbeatMonitor monitor = new HeartbeatMonitor();

    private final DiscoveryClient client;

    /**
     * Creates a new discovery client based name resolver factory.
     *
     * @param client The client to use for the address discovery.
     */
    public CustomDiscoveryClientResolverFactory(final DiscoveryClient client) {
        this.client = requireNonNull(client, "client");
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(final URI targetUri, final NameResolver.Args args) {
        if (DISCOVERY_SCHEME.equals(targetUri.getScheme())) {
            final String serviceName = targetUri.getPath();
            if (serviceName == null || serviceName.length() <= 1 || !serviceName.startsWith("/")) {
                throw new IllegalArgumentException("Incorrectly formatted target uri; "
                        + "expected: '" + DISCOVERY_SCHEME + ":[//]/<service-name>'; "
                        + "but was '" + targetUri.toString() + "'");
            }
            final CustomDiscoveryClientNameResolver nameResolver = newNameResolver(serviceName.substring(1), args);
            this.discoveryClientNameResolvers.add(nameResolver);
            return nameResolver;
        }
        return null;
    }

    /**
     * Factory method to create the resolver for the given service name.
     *
     * @param serviceName The service name to create it for.
     * @param args The NameResolver arguments to use.
     * @return A newly created DiscoveryClientNameResolver.
     */
    protected CustomDiscoveryClientNameResolver newNameResolver(final String serviceName, final NameResolver.Args args) {
        return new CustomDiscoveryClientNameResolver(serviceName, this.client, args,
                GrpcUtil.SHARED_CHANNEL_EXECUTOR, this.discoveryClientNameResolvers::remove);
    }

    @Override
    public String getDefaultScheme() {
        return DISCOVERY_SCHEME;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 6; // More important than DNS
    }

    /**
     * Triggers a refresh of the registered name resolvers.
     *
     * @param event The event that triggered the update.
     */
    @EventListener(HeartbeatEvent.class)
    public void heartbeat(final HeartbeatEvent event) {
        if (this.monitor.update(event.getValue())) {
            for (final CustomDiscoveryClientNameResolver discoveryClientNameResolver : this.discoveryClientNameResolvers) {
                discoveryClientNameResolver.refreshFromExternal();
            }
        }
    }

    /**
     * Cleans up the name resolvers.
     */
    @PreDestroy
    public void destroy() {
        this.discoveryClientNameResolvers.clear();
    }

    @Override
    public String toString() {
        return "CustomDiscoveryClientResolverFactory [scheme=" + getDefaultScheme() +
                ", discoveryClient=" + this.client + "]";
    }

}

