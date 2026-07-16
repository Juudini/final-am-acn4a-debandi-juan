package com.example.final_am_acn4a_debandi_juan.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreditsResponse {

    @SerializedName("cast")
    private List<CastMember> cast;

    public List<CastMember> getCast() {
        return cast;
    }
}
