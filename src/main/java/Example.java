public class Example {
    /*@ public normal_behavior
      @ requires x > 0;
      @ ensures \result > 0;
      @*/
    public int square(int x) {
        return x*x;
    }
}
