package fort.guide.fort;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by dj on 22/3/19.
 */

public interface JSONPlaceHolder {


    @GET("getPacketsById/{fortName}")
    Call<List<Packet>> getPackets(@Path("fortName") String fortName);

}
