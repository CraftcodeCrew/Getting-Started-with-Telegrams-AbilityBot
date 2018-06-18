package task;

public class MorningReminderTask implements DailyTask {

    private final Callback callback;

    public interface Callback {
        void onTimeForMorningTask();
    }

    public MorningReminderTask(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void execute() {
        callback.onTimeForMorningTask();
    }
}
