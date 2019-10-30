package dev.toppe.img.screencapture.uploader;

public class UploadLink {


    private final String imageLink;
    private final String webLink;

    public UploadLink(String imageLink, String webLink) {
        this.imageLink = imageLink;
        this.webLink = webLink;
    }

    /**
     * Link pointing directly to the image
     * @return
     */
    public String getImageLink() {
        return imageLink;
    }

    /**
     * Link pointing to the website (e.g includes views, upload date)
     * @return
     */
    public String getWebLink() {
        return webLink;
    }
}
