import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

import java.util.function.Consumer;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.api.objects.Update;

import task.DailyTaskExecutor;
import task.MorningReminderTask;

public class FitnessBot extends AbilityBot implements MorningReminderTask.Callback {

    private final ResponseHandler responseHandler;
    private final DailyTaskExecutor dailyTaskExecutor;

    public FitnessBot() {
        this(BotConfig.BOT_TOKEN, BotConfig.BOT_USERNAME);
    }

    private FitnessBot(String botToken, String botUsername) {
        super(botToken, botUsername);
        responseHandler = new ResponseHandler(sender, db);
        dailyTaskExecutor = new DailyTaskExecutor(new MorningReminderTask(this));
        dailyTaskExecutor.startExecutionAt(9, 0, 0);
    }

    public int creatorId() {
        return BotConfig.CREATOR_ID;
    }

    public Ability replyToStart() {
        return Ability
                .builder()
                .name("start")
                .info(Constants.START_DESCRIPTION)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx ->  responseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    public Reply replyToButtons() {
        Consumer<Update> action = upd -> responseHandler.replyToButtons(getChatId(upd), upd.getCallbackQuery().getData());
        return Reply.of(action, Flag.CALLBACK_QUERY);
    }

    @Override
    public void onTimeForMorningTask() {
        responseHandler.sayMorningMessages();
    }
}
