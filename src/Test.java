import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Test {

  private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<>();
  public static class ParseDate implements Runnable{
    int i=0;
    public ParseDate(int i){
      this.i =i;
    }
    public void run(){
      try {
        if(sdf.get() == null){
          sdf.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }
        Date t = sdf.get().parse("2017-02-30 19:29:" + i % 60);
        System.out.println(i + ":" + t);
      }catch (ParseException e){
        e.printStackTrace();
      }
    }
  }
  public static void main(String[] args) {
//    ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
//    LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    ExecutorService es = Executors.newFixedThreadPool(10);
    for (int i = 0; i < 1000 ; i++) {
      es.execute(new ParseDate(i));
    }
  }

}
