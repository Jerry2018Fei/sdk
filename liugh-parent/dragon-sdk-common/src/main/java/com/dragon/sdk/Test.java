package com.dragon.sdk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author dingpengfei
 * @date 2019-05-29 14:31
 */
public class Test {

  private static volatile Map<String, A> map = new ConcurrentHashMap<>();
  private static volatile Set<String> set = new ConcurrentSkipListSet<>();
  private static volatile Set<String> set2 = new ConcurrentSkipListSet<>();
  private static boolean bool = false;

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    List<MyTh> threads = new ArrayList<>();
    for (Integer i = 0; i < 10; i++) {
      MyTh th = new MyTh();
      threads.add(th);
      new Thread(th).start();
    }
    while (true) {
      Integer j = 0;
      for (MyTh th : threads) {
        if (th.bool) {
          j++;
        }
      }
//      new Thread(
//              () -> {
//                while (!bool) {
//                  Set<String> keys = map.keySet();
//                  for (String s : keys) {
//                    if (map.get(s).getTime() >= System.currentTimeMillis()) {
//                      map.remove(s);
//                      System.out.println("remove:" + s);
//                    }
//                  }
//                }
//              })
//          .start();
      if (j == threads.size()) {
        System.out.println("over");
        bool = true;
        break;
      }
    }
    System.out.println(map.size());
    System.out.println((System.currentTimeMillis() - start));
  }

  @Data
  public static class MyTh implements Runnable {
    private boolean bool = false;

    @Override
    public void run() {
      for (Integer k = 0; k < 100000; k++) {

        map.put(
            Thread.currentThread().getName() + "~" + "key" + k,
            new A(UUID.randomUUID().toString(), System.currentTimeMillis() + 100));
        System.out.println(
            Thread.currentThread().getName()
                + "~"
                + "key"
                + k
                + "==>"
                + UUID.randomUUID().toString());
        System.out.println(map.size());
        if (k % 100 == 0) {
          try {
            Thread.sleep(1);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      bool = true;
    }
  }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class A {
  private String val;
  private Long time;
}
