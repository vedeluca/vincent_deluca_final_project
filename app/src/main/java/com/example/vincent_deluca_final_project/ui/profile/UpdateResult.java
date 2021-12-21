package com.example.vincent_deluca_final_project.ui.profile;

import androidx.annotation.Nullable;

public class UpdateResult {
    @Nullable
    private Exception error;
    private boolean hasDisplayName;
    private boolean hasProfilePicture;

    UpdateResult(@Nullable Exception error) {
        this.error = error;
    }

    UpdateResult(boolean hasProfilePicture, boolean hasDisplayName) {
        this.hasDisplayName = hasDisplayName;
        this.hasProfilePicture = hasProfilePicture;
    }

    Boolean getHasProfilePicture() {
        return hasProfilePicture;
    }

    Boolean getHasDisplayName() {
        return hasDisplayName;
    }

    Boolean getHasBoth(){
        return hasDisplayName && hasProfilePicture;
    }

    @Nullable
    Exception getError() {
        return error;
    }
}
