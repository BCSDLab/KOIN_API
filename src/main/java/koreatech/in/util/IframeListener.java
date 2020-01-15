package koreatech.in.util;

import com.nhncorp.lucy.security.xss.event.ElementListener;
import com.nhncorp.lucy.security.xss.listener.SecurityUtils;
import com.nhncorp.lucy.security.xss.listener.WhiteUrlList;
import com.nhncorp.lucy.security.xss.markup.Element;

public class IframeListener implements ElementListener {
    public IframeListener() {
    }

    public void handleElement(Element element) {
        if (!element.isDisabled()) {
            String srcUrl = element.getAttributeValue("src");
            boolean isWhiteUrl = this.isWhiteUrl(srcUrl);
            boolean isVulnerable = SecurityUtils.checkVulnerable(element, srcUrl, isWhiteUrl);
            if (isVulnerable) {
                element.setEnabled(false);
            }
            else {
                if (!isWhiteUrl && !srcUrl.isEmpty()) {
                    element.setEnabled(false);
                }
            }
        }
    }

    private boolean isWhiteUrl(String url) {
        WhiteUrlList list = WhiteUrlList.getInstance();
        return list != null && list.contains(url);
    }
}
