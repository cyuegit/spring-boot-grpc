package guava;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: TableTest
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/24 13:54
 */
public class TableTest {

    @Test
    public void cellsTest(){
        //行，列，值
        HashBasedTable<Object, Object, Object> table = HashBasedTable.create();
        table.put("192.168.11.1:9080",1,"SERVICE1");
        table.put("192.168.11.1:9088",1,"SERVICE2");
        table.put("192.168.11.1:9808",5,"SERVICE3");
        Set<Table.Cell<Object, Object, Object>> cells = table.cellSet();
        cells.forEach(e->{
            System.out.println(e.getRowKey()+"-"+e.getColumnKey()+": "+e.getValue());
        });
    }

    @Test
    public void rowAndColumnTest(){
        HashBasedTable<String,String,String> table = HashBasedTable.create();
        table.put("IBM", "101", "xiaoming");
        table.put("IBM", "102", "xiaohong");
        table.put("IBM", "103", "xiaomei");

        table.put("MicroSoft", "201", "daming");
        table.put("MicroSoft", "202", "dahong");
        table.put("MicroSoft", "203", "damei");

        table.put("Suning", "301", "zhangsan");
        table.put("Suning", "302", "lisi");
        table.put("Suning", "303", "wangwu");

        Set<String> rowSet = table.rowKeySet();
        rowSet.forEach(row->{
            System.out.println("rowKey: "+row);
            Map<String, String> columnMap = table.row(row);
            columnMap.forEach((columnKey,value)->{
                System.out.println("columnKey: "+columnKey+", value: "+value);
            });
        });

        Map<String, String> rowMap = table.column("102");
        rowMap.forEach((row,value)->{
            System.out.println("row: "+row+", value: "+value);
        });
    }

    @Test
    public void tableCRUD(){
        HashBasedTable<Integer,String,String> table = HashBasedTable.create();
        table.put(1, "A", "zhangsan");
        table.put(1, "B", "female");
        table.put(1, "C", "beijing");
        table.put(2, "A","lisi" );
        table.put(2, "B", "male");

        Assert.assertFalse(table.contains(2, "C"));
        Assert.assertTrue(table.containsRow(1));
        Assert.assertTrue(table.containsColumn("B"));
        Assert.assertTrue(table.containsValue("beijing"));

        System.out.println(table.get(1, "C"));  //beijing
        table.remove(1, "C");
        System.out.println(table.get(1, "C"));  //null

        Map<String, String> columnMap = table.row(1);
        System.out.println(columnMap);      //{A=zhangsan, B=female}
        Map<Integer, String> rowMap = table.column("B");
        System.out.println(rowMap);         //{1=female, 2=male}

        Set<Table.Cell<Integer, String, String>> cells = table.cellSet();
        cells.forEach(e->{
            System.out.println(e.getRowKey()+"-"+e.getColumnKey()+": "+e.getValue());
        });
        //        1-A: zhangsan
        //        1-B: female
        //        2-A: lisi
        //        2-B: male
    }

    @Test
    public void testr() {
        Map<String, Object> copyMap = new HashMap<>(16);
        copyMap.put("s",111);
        copyMap.put("rts",33);
        copyMap.remove("ticket");
        copyMap.remove("s");
        System.out.println(copyMap);
    }
}

