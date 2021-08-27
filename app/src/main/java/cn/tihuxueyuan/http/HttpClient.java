package cn.tihuxueyuan.http;

import android.graphics.Bitmap;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.tihuxueyuan.model.CourseFileList;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import cn.tihuxueyuan.model.SearchMusic;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.model.CourseList;
import okhttp3.RequestBody;

public class HttpClient {
    private static final String SPLASH_URL = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
    //    http://tingapi.ting.baidu.com/v1/restserver/ting
//    private static final String BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting";
    private static final String BASE_URL = "http://10.0.2.2:8082/api/";
//    private static final String BASE_URL = "http://47.102.146.8:8082/api/";
    //    private static final String BASE_URL = "http://10.0.2.2:8082/api/findCourseFileByCourseIdOk";
    private static final String METHOD_GET_MUSIC_LIST = "baidu.ting.billboard.billList";
    private static final String METHOD_DOWNLOAD_MUSIC = "baidu.ting.song.play";
    private static final String METHOD_ARTIST_INFO = "baidu.ting.artist.getInfo";
    private static final String METHOD_SEARCH_MUSIC = "baidu.ting.search.catalogSug";
    private static final String METHOD_LRC = "baidu.ting.song.lry";
    private static final String PARAM_METHOD = "method";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_SONG_ID = "songid";
    private static final String PARAM_TING_UID = "tinguid";
    private static final String PARAM_QUERY = "query";

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public static void searchMusic(String keyword, final HttpCallback<SearchMusic> callback) {
//        OkHttpUtils.get().url(BASE_URL)
        OkHttpUtils.post().url("http://10.0.2.2:8082/api/findCourseFileByCourseIdOk")
                .addParams("id", "1")
//                .addParams(PARAM_QUERY, keyword)
                .build()

                .execute(new JsonCallback<SearchMusic>(SearchMusic.class) {
                    @Override
                    public void onResponse(SearchMusic response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getCourseTypes(String keyword, final HttpCallback<CourseTypeList> callback) {
        OkHttpUtils.post().url(BASE_URL + "getCourseTypesOk")
                .build()
                .execute(new JsonCallback<CourseTypeList>(CourseTypeList.class) {
                    @Override
                    public void onResponse(CourseTypeList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getLatest(String keyword, final HttpCallback<CourseFileList> callback) {
        OkHttpUtils.post().url(BASE_URL + "getLatest")
                .build()
                .execute(new JsonCallback<CourseFileList>(CourseFileList.class) {
                    @Override
                    public void onResponse(CourseFileList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getCourseFilesByCourseId(String courseId, final HttpCallback<CourseFileList> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("course_id", courseId);

        OkHttpUtils.post().url(BASE_URL + "findCourseFileByCourseIdOk")
                .params(params)
                .build()
                .execute(new JsonCallback<CourseFileList>(CourseFileList.class) {
                    @Override
                    public void onResponse(CourseFileList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getCourseByTypeId(String typeId, final HttpCallback<CourseList> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("id", typeId);

        //  RequestBody r = RequestBody.create(MediaType.parse("application/json"), "aa");
        OkHttpUtils.post().url(BASE_URL + "findCourseByTypeIdOk")
                .params(params)
                .build()
                .execute(new JsonCallback<CourseList>(CourseList.class) {
                    @Override
                    public void onResponse(CourseList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
}

//    public static void getSplash( final HttpCallback<Splash> callback) {
//        OkHttpUtils.get().url(SPLASH_URL).build()
//                .execute(new JsonCallback<Splash>(Splash.class) {
//                    @Override
//                    public void onResponse(Splash response, int id) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        callback.onFail(e);
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        callback.onFinish();
//                    }
//                });
//    }
//
//    public static void downloadFile(String url, String destFileDir, String destFileName,  final HttpCallback<File> callback) {
//        OkHttpUtils.get().url(url).build()
//                .execute(new FileCallBack(destFileDir, destFileName) {
//                    @Override
//                    public void inProgress(float progress, long total, int id) {
//                    }
//
//                    @Override
//                    public void onResponse(File file, int id) {
//                        if (callback != null) {
//                            callback.onSuccess(file);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        if (callback != null) {
//                            callback.onFail(e);
//                        }
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        if (callback != null) {
//                            callback.onFinish();
//                        }
//                    }
//                });
//    }
//
//    public static void getSongListInfoOld(String type, int size, int offset, @NonNull final HttpCallback<OnlineMusicList> callback) {
//        OkHttpUtils.get().url(BASE_URL)
//                .addParams(PARAM_METHOD, METHOD_GET_MUSIC_LIST)
//                .addParams(PARAM_TYPE, type)
//                .addParams(PARAM_SIZE, String.valueOf(size))
//                .addParams(PARAM_OFFSET, String.valueOf(offset))
//                .build()
//                .execute(new JsonCallback<OnlineMusicList>(OnlineMusicList.class) {
//                    @Override
//                    public void onResponse(OnlineMusicList response, int id) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        callback.onFail(e);
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        callback.onFinish();
//                    }
//                });
//    }
//
//
//    public static void getSongListInfo(String type, int size, int offset, @NonNull final HttpCallback<OnlineMusicListA> callback) {
////        OkHttpUtils.post().url("http://localhost:8082/api/findCourseFileByCourseIdOk")
//        OkHttpUtils.post().url("http://10.0.2.2:8082/api/findCourseFileByCourseIdOk")
//
//                .addParams("id", "1")
////                .addParams(PARAM_TYPE, type)
////                .addParams(PARAM_SIZE, String.valueOf(size))
////                .addParams(PARAM_OFFSET, String.valueOf(offset))
//                .build()
//                .execute(new JsonCallback<OnlineMusicListA>(OnlineMusicListA.class) {
////                    @Override
////                    public void onResponse(OnlineMusicList response, int id) {
////                        callback.onSuccess(response);
////                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        callback.onFail(e);
//                    }
//
//                    @Override
//                    public void onResponse(OnlineMusicListA response, int id) {
//                        int i;
//                        Log.i("execute tag1: ", response.toString());
//                        Log.i("execute tag1: title= ", response.getTitle());
////                        callback.onSuccess(response);
//
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        callback.onFinish();
//                    }
//                });
//    }

//    public static void getMusicDownloadInfo(String songId, @NonNull final HttpCallback<DownloadInfo> callback) {
//        OkHttpUtils.get().url(BASE_URL)
//                .addParams(PARAM_METHOD, METHOD_DOWNLOAD_MUSIC)
//                .addParams(PARAM_SONG_ID, songId)
//                .build()
//                .execute(new JsonCallback<DownloadInfo>(DownloadInfo.class) {
//                    @Override
//                    public void onResponse(DownloadInfo response, int id) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        callback.onFail(e);
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        callback.onFinish();
//                    }
//                });
//    }
//
//    public static void getBitmap(String url, @NonNull final HttpCallback<Bitmap> callback) {
//        OkHttpUtils.get().url(url).build()
//                .execute(new BitmapCallback() {
//                    @Override
//                    public void onResponse(Bitmap bitmap, int id) {
//                        callback.onSuccess(bitmap);
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        callback.onFail(e);
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        callback.onFinish();
//                    }
//                });
//    }
//
//    public static void getLrc(String songId, @NonNull final HttpCallback<Lrc> callback) {
//        OkHttpUtils.get().url(BASE_URL)
//                .addParams(PARAM_METHOD, METHOD_LRC)
//                .addParams(PARAM_SONG_ID, songId)
//                .build()
//                .execute(new JsonCallback<Lrc>(Lrc.class) {
//                    @Override
//                    public void onResponse(Lrc response, int id) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        callback.onFail(e);
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        callback.onFinish();
//                    }
//                });
//    }
//
//    public static void searchMusic(String keyword, @NonNull final HttpCallback<SearchMusic> callback) {
//        OkHttpUtils.get().url(BASE_URL)
//                .addParams(PARAM_METHOD, METHOD_SEARCH_MUSIC)
//                .addParams(PARAM_QUERY, keyword)
//                .build()
//                .execute(new JsonCallback<SearchMusic>(SearchMusic.class) {
//                    @Override
//                    public void onResponse(SearchMusic response, int id) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        callback.onFail(e);
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        callback.onFinish();
//                    }
//                });
//    }
//
//    public static void getArtistInfo(String tingUid, @NonNull final HttpCallback<ArtistInfo> callback) {
//        OkHttpUtils.get().url(BASE_URL)
//                .addParams(PARAM_METHOD, METHOD_ARTIST_INFO)
//                .addParams(PARAM_TING_UID, tingUid)
//                .build()
//                .execute(new JsonCallback<ArtistInfo>(ArtistInfo.class) {
//                    @Override
//                    public void onResponse(ArtistInfo response, int id) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        callback.onFail(e);
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        callback.onFinish();
//                    }
//                });
//    }
//}
