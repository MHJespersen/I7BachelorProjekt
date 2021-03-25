package mmm.i7bachelor_smartsale.app.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccessToken {

    @SerializedName("access_token")
    @Expose
    private String access_token;
    @SerializedName("expires_in")
    @Expose
    private String expires_in;
    @SerializedName("token_type")
    @Expose
    private String token_type;
    @SerializedName("scope")
    @Expose
    private String scope;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public String setExpires_in(String expires_in){return this.expires_in = expires_in;}

    public String getToken_type() {
        return token_type;
    }

    public String setToken_type(String token_type){return this.token_type = token_type;}

    public String getScope() {
        return scope;
    }

    public String setScope(String scope){return this.scope = scope;}


}
