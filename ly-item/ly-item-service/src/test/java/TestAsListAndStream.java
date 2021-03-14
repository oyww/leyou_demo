import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestAsListAndStream {
    @Test
    public void testAsList() throws Exception {
        String id1 = "1";
        String id2 = "2";
        String id3 = "3";
        List<String> list = Arrays.asList(id1, id2, id3);
        System.out.println(list);
        Stream<String> stream = list.stream().map(obj -> "id" + obj);
        Optional<String> reduce = stream.reduce((s1, s2) -> s1 + "/" + s2);
        String s = reduce.get();
        String join = StringUtils.join(list, "/");
        System.out.println("lang3字符串工具类拼接，效率高" + join);
        System.out.println("流拼接：（比较消耗内存,可以加工内容）" + s);


    }

    @Test
    public void testMap() throws Exception {
        List list1 = new ArrayList();
        for (int i = 0; i < 100; i++) {
            list1.add(i);
        }
        Map<String, String> stringMap = Stream.of("a", "b", "c", "a").collect(Collectors.toMap(x -> x, x -> x + x, (oldVal, newVal) -> newVal));
        stringMap.forEach((k, v) -> System.out.println(k + ":" + v));


    }

    @Test
    public void testLongSerializable() throws Exception {
        Long l = 1l;
        ArrayList<Serializable> list = new ArrayList<>();
        list.add(l);
    }

    @Test
    public void testFilter() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");
        map.put("d", "4");
        String collect = map.entrySet().stream().filter(m -> m.getKey() != "d").map(m -> m.getValue()).collect(Collectors.joining(","));
        System.out.println(collect);//1,2,3

    }

    @Test
    public void testFilterValue() throws Exception {
        //按值过滤
        Map<Integer, String> HOSTING = new HashMap<>();
        HOSTING.put(1, "linode.com");
        HOSTING.put(2, "heroku.com");
        HOSTING.put(3, "digitalocean.com");
        HOSTING.put(4, "aws.amazon.com");
        String result = HOSTING.entrySet().stream()
                .filter(x -> {
                    if (!x.getValue().contains("amazon") && !x.getValue().contains("digital")) {
                        return true;
                    }
                    return false;
                })
                .map(map -> map.getValue())
                .collect(Collectors.joining(","));
        System.out.println("With Java 8 : " + result);
        //With Java 8 : linode.com,heroku.com
    }
    @Test
    public void testFilterKey()throws Exception{
        Map<Integer, String> HOSTING = new HashMap<>();
        HOSTING.put(1, "linode.com");
        HOSTING.put(2, "heroku.com");
        HOSTING.put(3, "digitalocean.com");
        HOSTING.put(4, "aws.amazon.com");

        //Map -> Stream -> Filter -> Map
        Map<Integer, String> result1 = HOSTING.entrySet().stream()
                .filter(map -> map.getKey() == 2)
                .collect(Collectors.toMap(h -> h.getKey(), h -> h.getValue()));

        System.out.println(result1);

        Map<Integer, String> result2 = HOSTING.entrySet().stream()
                .filter(map -> {
                    Integer key = map.getKey();
                    if (key != 4) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println(result2);
    }

    public static <K, V> Map<K, V> filterByValue(Map<K, V> map, Predicate<V> predicate) {
        return map.entrySet().stream()
                .filter(x -> predicate.test(x.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    @Test
    public void testMapFilter4()throws Exception{

            Map<Integer, String> HOSTING = new HashMap<>();
            HOSTING.put(1, "linode.com");
            HOSTING.put(2, "heroku.com");
            HOSTING.put(3, "digitalocean.com");
            HOSTING.put(4, "aws.amazon.com");
            HOSTING.put(5, "aws2.amazon.com");

            Map<Integer, String> result1 = filterByValue(HOSTING, x -> x.contains("heroku"));
            System.out.println(result1);

            Map<Integer, String> result2 = filterByValue(HOSTING, x -> (x.contains("aws") || x.contains("digitalocean")));
            System.out.println(result2);

            Map<Integer, String> result3 = filterByValue(HOSTING, x -> (x.contains("aws") && !x.contains("aws2")));
            System.out.println(result3);

            Map<Integer, String> result4 = filterByValue(HOSTING, x -> x.length() <= 10);
            System.out.println(result4);

    }
    @Test
    public void testIntefaceLambda()throws Exception{
        say(()-> System.out.println("接口"));
    }
    public void say(Person p){
        p.say();

    }

}
