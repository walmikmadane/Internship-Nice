package entity;

public class BotResponse
{
    String msg;
    boolean learning;

    public BotResponse(String msg, boolean learning) {
        this.msg = msg;
        this.learning = learning;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setLearning(boolean learning) {
        this.learning = learning;
    }

    public String getMsg() {

        return msg;
    }

    public boolean isLearning() {
        return learning;
    }
}
