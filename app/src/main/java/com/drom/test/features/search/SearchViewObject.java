package com.drom.test.features.search;

public class SearchViewObject {
    public final boolean inProgress;
    public final boolean isPaginationProgress;
    public final String error;

    private SearchViewObject(boolean inProgress, boolean isPaginationProgress, String error) {
        this.inProgress = inProgress;
        this.isPaginationProgress = isPaginationProgress;
        this.error = error;
    }

    public static SearchViewObject success() {
        return new SearchViewObject(false, false, null);
    }

    public static SearchViewObject progress() {
        return new SearchViewObject(true, false, null);
    }

    public static SearchViewObject paginationProgress() {
        return new SearchViewObject(false, true, null);
    }

    public static SearchViewObject error(String error) {
        return new SearchViewObject(false, false, error);
    }
}
