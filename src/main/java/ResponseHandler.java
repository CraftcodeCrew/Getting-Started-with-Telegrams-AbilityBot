import java.util.Map;

import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class ResponseHandler {

    private final MessageSender sender;
    private final Map<Long, State> chatStates;

    public ResponseHandler(MessageSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap(Constants.CHAT_STATES);
    }

    public void replyToStart(long chatId) {
        try {
            sender.execute(new SendMessage()
                    .setText(Constants.START_REPLY)
                    .setChatId(chatId));

            sender.execute(new SendMessage()
                    .setText(Constants.FIND_TRAINING_DATE)
                    .setChatId(chatId)
                    .setReplyMarkup(KeyboardFactory.withTodayTomorrowButtons()));

            chatStates.put(chatId, State.AWAITING_TRAINING_DAY);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void replyToButtons(long chatId, String buttonId) {
        try {
            switch (buttonId) {
                case Constants.TRAINING_TODAY:
                    replyToTrainingToday(chatId);
                    break;
                case Constants.TRAINING_TOMORROW:
                    replyToTrainingTomorrow(chatId);
                    break;
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        System.out.println("Chat state for user " + chatId + " is " + chatStates.get(chatId));
    }

    private void replyToTrainingToday(long chatId) throws TelegramApiException {
        if (chatStates.get(chatId).equals(State.AWAITING_TRAINING_DAY)) {
            sender.execute(new SendMessage()
                    .setText(Constants.TRAINING_TODAY_REPLY)
                    .setChatId(chatId));
            chatStates.put(chatId, State.TODAY_IS_TRAINING_DAY);
        }

    }

    private void replyToTrainingTomorrow(long chatId) throws TelegramApiException {
        if (chatStates.get(chatId).equals(State.AWAITING_TRAINING_DAY)) {
            sender.execute(new SendMessage()
                    .setText(Constants.TRAINING_TOMORROW_REPLY)
                    .setChatId(chatId));
            chatStates.put(chatId, State.TODAY_IS_RELAX_DAY);
        }
    }

    public void sayMorningMessages() {
        try {
            for (long chatId : chatStates.keySet()) {
                switch (chatStates.get(chatId)) {
                    case TODAY_IS_TRAINING_DAY:
                        processTrainingDay(chatId);
                        break;
                    case TODAY_IS_RELAX_DAY:
                        processRelaxDay(chatId);
                        break;
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void processTrainingDay(long chatId) throws TelegramApiException {
        sender.execute(new SendMessage()
                .setText(Constants.TRAINING_REMINDER)
                .setChatId(chatId));
        chatStates.put(chatId, State.TODAY_IS_RELAX_DAY);

    }

    private void processRelaxDay(long chatId) {
        chatStates.put(chatId, State.TODAY_IS_TRAINING_DAY);
    }
}
