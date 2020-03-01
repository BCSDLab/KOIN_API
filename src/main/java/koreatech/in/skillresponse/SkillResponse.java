package koreatech.in.skillresponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.PreconditionFailedException;

public class SkillResponse {
    private final String version = "2.0"; // 스킬 응답의 version, <major-version>.<minor-version>의 모습
    private JsonObject skillPayload = new JsonObject(); // 전체 JSON
    private JsonObject template = new JsonObject(); // JSON - template
    private JsonArray outputs = new JsonArray(); // JSON - template - outputs
    private JsonArray quickReplies = new JsonArray(); // JSON - template - outputs

    public SkillResponse() {
        skillPayload.addProperty("version", version);
        skillPayload.add("template", template);
        template.add("outputs", outputs);
        template.add("quickReplies", quickReplies);
    }

    public JsonElement getSkillPayload() throws PreconditionFailedException {
//        skillPayload.remove("template"); // remove : exception-safe
//        template.remove("outputs");
//        template.remove("quickReplies");
//        template.add("outputs", outputs);
//        template.add("quickReplies", quickReplies);
//        skillPayload.add("template", template);

        if (outputs.size() == 0) throw new PreconditionFailedException(new ErrorMessage("outputs는 필수 항목입니다.", 0));
        return this.skillPayload;
    }

    public void addSimpleText(String text) throws PreconditionFailedException {
        if (outputs.size() >= 3) throw new PreconditionFailedException(new ErrorMessage("outputs의 제한은 1개 이상 3개 이하입니다.", 0));
        JsonObject field = new JsonObject();
        JsonObject type = new JsonObject();
        field.addProperty("text", text);
        type.add(KakaoBot.TemplateType.SIMPLETEXT.getTypeText(), field);

        outputs.add(type);
    }

    public void addSimpleImage(String imageUrl, String altText) throws PreconditionFailedException {
        if (outputs.size() >= 3) throw new PreconditionFailedException(new ErrorMessage("outputs의 제한은 1개 이상 3개 이하입니다.", 0));
        JsonObject field = new JsonObject();
        JsonObject type = new JsonObject();
        field.addProperty("imageUrl", imageUrl);
        field.addProperty("altText", altText);
        type.add(KakaoBot.TemplateType.SIMPLEIMAGE.getTypeText(), field);

        outputs.add(type);
    }

    public void addBasicCard(JsonElement cardProperties) throws PreconditionFailedException {
        if (outputs.size() >= 3) throw new PreconditionFailedException(new ErrorMessage("outputs의 제한은 1개 이상 3개 이하입니다.", 0));
        outputs.add(cardProperties);
    }

    public static class BasicCardBuilder {
        private String title; // 최대 2줄
        private String description; // 최대 230자
        private JsonObject thumbnail;
        //        private JsonObject profile;
//        private JsonObject social; // social과 profile은 현재 미지원 상태
        private JsonArray buttons = new JsonArray(); // 최대 3개

        public BasicCardBuilder title(String title) throws PreconditionFailedException {
            if (title.split("\n").length >= 2) throw new PreconditionFailedException(new ErrorMessage("title은 최대 2줄 제한입니다.", 0));
            this.title = title;
            return this;
        }

        public BasicCardBuilder description(String description) throws PreconditionFailedException {
            if (description.length() > 230) throw new PreconditionFailedException(new ErrorMessage("description의 길이는 최대 230자 제한입니다.", 0));
            this.description = description;
            return this;
        }

        public BasicCardBuilder thumbnail(JsonObject thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public BasicCardBuilder buttons(JsonObject button) throws PreconditionFailedException {
            if (buttons.size() >= 3) throw new PreconditionFailedException(new ErrorMessage("Button의 개수는 최대 3개 제한입니다.", 0));
            buttons.add(button);
            return this;
        }

        public JsonElement build() {
            JsonObject field = new JsonObject();
            JsonObject type = new JsonObject();
            if (title != null) field.addProperty("title", title);
            if (description != null) field.addProperty("description", description);
            if (thumbnail != null) field.add("thumbnail", thumbnail);
            if (buttons.size() != 0) field.add("buttons", buttons);
            type.add(KakaoBot.TemplateType.BASICCARD.getTypeText(), field);
            return type;
        }
    }

    public void addQujckReplies(String label, String action, String messageText) throws PreconditionFailedException {
        if (quickReplies.size() >= 10) throw new PreconditionFailedException(new ErrorMessage("quickReplies의 제한은 10개 이하입니다.", 0));
        if (!action.equals(KakaoBot.QuickRepliesActionType.MESSAGE.getTypeText()))
            throw new PreconditionFailedException(new ErrorMessage("quickReplies의 action이 올바르게 설정되지 않았습니다.", 0));
        if (label.length() > 8) throw new PreconditionFailedException(new ErrorMessage("quickReplies에서 label은 최대 8자 제한입니다.", 0));

        JsonObject field = new JsonObject();
        field.addProperty("action", action);
        field.addProperty("label", label);
        field.addProperty("messageText", messageText);

        quickReplies.add(field);
    }

    public void addQujckReplies(String label, String action, String messageText, String blockId) throws Exception {
        if (quickReplies.size() >= 10) throw new PreconditionFailedException(new ErrorMessage("quickReplies의 제한은 10개 이하입니다.", 0));
        if (!action.equals(KakaoBot.QuickRepliesActionType.BLOCK.getTypeText()))
            throw new PreconditionFailedException(new ErrorMessage("quickReplies의 action이 올바르게 설정되지 않았습니다.", 0));
        if (label.length() > 8) throw new PreconditionFailedException(new ErrorMessage("quickReplies에서 label은 최대 8자 제한입니다.", 0));

        JsonObject field = new JsonObject();
        field.addProperty("action", action);
        field.addProperty("label", label);
        field.addProperty("messageText", messageText);
        field.addProperty("blockId", blockId);

        quickReplies.add(field);
    }

    public static class ThumbnailBuilder {
        private String imageUrl;
        private String link;
        private Boolean fixedRatio = false;
        private Integer width;
        private Integer height;

        public ThumbnailBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ThumbnailBuilder link(String link) {
            this.link = link;
            return this;
        }

        public ThumbnailBuilder fixedRatio(Boolean fixedRatio) {
            this.fixedRatio = fixedRatio;
            return this;
        }

        public ThumbnailBuilder width(Integer width) {
            this.width = width;
            return this;
        }

        public ThumbnailBuilder height(Integer height) {
            this.height = height;
            return this;
        }

        public JsonObject build() throws PreconditionFailedException {
            if (imageUrl == null) throw new PreconditionFailedException(new ErrorMessage("Thumbnail에서 imageUrl은 필수 설정 항목입니다.", 0));
            JsonObject thumbnail = new JsonObject();
            thumbnail.addProperty("imageUrl", imageUrl);
            if (link != null) thumbnail.addProperty("link", link);
            if (fixedRatio) {
                if (width == null || height == null)
                    throw new PreconditionFailedException(new ErrorMessage("Thumbnail에서 fixedRatio가 true일 경우, width와 height는 필수 설정 항목입니다.", 0));
                thumbnail.addProperty("fixedRatio", fixedRatio);
                thumbnail.addProperty("width", width);
                thumbnail.addProperty("height", height);
            }
            return thumbnail;
        }
    }

    public static class ButtonsBuilder {
        private String label; // 최대 8자
        private String action;
        private String webLinkUrl; // -> webLink
        private String osLink; // -> osLink
        private String messageText; // -> message or block
        private String phoneNumber; // -> phone
        private String blockId; // -> block
//        private Map<String, Object> extra = new HashMap<>();

        public ButtonsBuilder label(String label) throws PreconditionFailedException {
            if (label.length() > 8) throw new PreconditionFailedException(new ErrorMessage("Button에서 label은 최대 8자 제한입니다.", 0));
            this.label = label;
            return this;
        }

        public ButtonsBuilder action(String action) {
            this.action = action;
            return this;
        }

        public ButtonsBuilder webLinkUrl(String webLinkUrl) {
            this.webLinkUrl = webLinkUrl;
            return this;
        }

        public ButtonsBuilder osLink(String osLink) {
            this.osLink = osLink;
            return this;
        }

        public ButtonsBuilder messageText(String messageText) {
            this.messageText = messageText;
            return this;
        }

        public ButtonsBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public ButtonsBuilder blockId(String blockId) {
            this.blockId = blockId;
            return this;
        }

        public JsonObject build() throws PreconditionFailedException {
            if (label == null || action == null)
                throw new PreconditionFailedException(new ErrorMessage("Button에서 label과 action은 필수 설정 항목입니다.", 0));
            JsonObject button = new JsonObject();
            button.addProperty("action", action);
            button.addProperty("label", label);
            if (action.equals(KakaoBot.ButtonActionType.WEBLINK.getTypeText())) {
                if (webLinkUrl == null)
                    throw new PreconditionFailedException(new ErrorMessage("Button에서 action이 webLink일 경우, webLinkUrl은 필수 설정 항목입니다.", 0));
                button.addProperty("webLinkUrl", webLinkUrl);
            } else if (action.equals(KakaoBot.ButtonActionType.OSLINK.getTypeText())) {
                if (osLink == null)
                    throw new PreconditionFailedException(new ErrorMessage("Button에서 action이 osLink일 경우, osLink는 필수 설정 항목입니다.", 0));
                button.addProperty("osLink", osLink);
            } else if (action.equals(KakaoBot.ButtonActionType.MESSAGE.getTypeText())) {
                if (messageText == null)
                    throw new PreconditionFailedException(new ErrorMessage("Button에서 action이 message일 경우, messageText는 필수 설정 항목입니다.", 0));
                button.addProperty("messageText", messageText);
            } else if (action.equals(KakaoBot.ButtonActionType.PHONE.getTypeText())) {
                if (phoneNumber == null)
                    throw new PreconditionFailedException(new ErrorMessage("Button에서 action이 phone일 경우, phoneNumber은 필수 설정 항목입니다.", 0));
                button.addProperty("phoneNumber", phoneNumber);
            } else if (action.equals(KakaoBot.ButtonActionType.BLOCK.getTypeText())) {
                if (phoneNumber == null)
                    throw new PreconditionFailedException(new ErrorMessage("Button에서 action이 block일 경우, blockId은 필수 설정 항목입니다.", 0));
                button.addProperty("blockId", blockId);
            } else throw new PreconditionFailedException(new ErrorMessage("Button의 action이 올바르게 설정되지 않았습니다.", 0));

            return button;
        }
    }
}

