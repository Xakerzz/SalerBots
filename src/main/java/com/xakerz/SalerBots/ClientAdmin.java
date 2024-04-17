package com.xakerz.SalerBots;

public class ClientAdmin extends Client{
    private boolean imageUrls;
    private boolean captionsForImage;
    private boolean audioUrls;
    private boolean captionsAudio;
    private boolean captionsCommonPosts;
    private boolean imageCommonPostsUrls;
    private boolean opros;
    private boolean TestImageUrls;
    private boolean TestText;
    private boolean ImageAdsPostsUrls;
    private boolean TextAdsPosts;
    public ClientAdmin(String name, String nickName, long idClient, boolean questionToAdmin) {
        super(name, nickName, idClient, questionToAdmin);
    }

    public boolean isImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(boolean imageUrls) {
        this.imageUrls = imageUrls;
    }

    public boolean isCaptionsForImage() {
        return captionsForImage;
    }

    public void setCaptionsForImage(boolean captionsForImage) {
        this.captionsForImage = captionsForImage;
    }

    public boolean isAudioUrls() {
        return audioUrls;
    }

    public void setAudioUrls(boolean audioUrls) {
        this.audioUrls = audioUrls;
    }

    public boolean isCaptionsAudio() {
        return captionsAudio;
    }

    public void setCaptionsAudio(boolean captionsAudio) {
        this.captionsAudio = captionsAudio;
    }

    public boolean isCaptionsCommonPosts() {
        return captionsCommonPosts;
    }

    public void setCaptionsCommonPosts(boolean captionsCommonPosts) {
        this.captionsCommonPosts = captionsCommonPosts;
    }

    public boolean isImageCommonPostsUrls() {
        return imageCommonPostsUrls;
    }

    public void setImageCommonPostsUrls(boolean imageCommonPostsUrls) {
        this.imageCommonPostsUrls = imageCommonPostsUrls;
    }

    public boolean isOpros() {
        return opros;
    }

    public void setOpros(boolean opros) {
        this.opros = opros;
    }

    public boolean isTestImageUrls() {
        return TestImageUrls;
    }

    public void setTestImageUrls(boolean testImageUrls) {
        TestImageUrls = testImageUrls;
    }

    public boolean isTestText() {
        return TestText;
    }

    public void setTestText(boolean testText) {
        TestText = testText;
    }

    public boolean isImageAdsPostsUrls() {
        return ImageAdsPostsUrls;
    }

    public void setImageAdsPostsUrls(boolean imageAdsPostsUrls) {
        ImageAdsPostsUrls = imageAdsPostsUrls;
    }

    public boolean isTextAdsPosts() {
        return TextAdsPosts;
    }

    public void setTextAdsPosts(boolean textAdsPosts) {
        TextAdsPosts = textAdsPosts;
    }
}
