package koreatech.in.domain.PageResource;

import io.swagger.annotations.ApiModelProperty;

public class PageResource {
    @ApiModelProperty(notes = "카드 뉴스 이미지 url", example = "http://templink.com/test")
    private String card_news_image;
    @ApiModelProperty(notes = "카드 뉴스 링크 url", example = "http://templink.com/test")
    private String card_link_url;

    public String getCard_news_image() { return card_news_image; }

    public void setCard_news_image(String card_news_image) { this.card_news_image = card_news_image; }

    public String getCard_link_url() { return card_link_url; }

    public void setCard_link_url(String card_link_url) { this.card_link_url = card_link_url; }

    public void update(PageResource pageResource) {
        if(pageResource.card_news_image != null) {
            this.card_news_image = pageResource.card_news_image;
        }

        if(pageResource.card_link_url != null) {
            this.card_link_url = pageResource.card_link_url;
        }
    }

    @Override
    public String toString() {
        return "PageResource{" +
                "card_news_image='" + card_news_image + '\'' +
                ", card_link_url='" + card_link_url + '\'' +
                '}';
    }
}