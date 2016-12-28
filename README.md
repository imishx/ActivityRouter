###主要功能
1. 统一Hyrid App开发过程中的跳转,http、https的schema由webview打开，自定义的schema打开本地Activity。
2. 为Activity配置拦截器，解耦跳转时业务逻辑判断。

### 简单例子
####初始化
```java

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化并指定schema
        Routers.init("schema");
    }
}

```
####使用ActivityMapping注解目标Activity的URL，还可以指定该Activity接受的参数
```java

@ActivityMapping(value = {"product/detail"}, stringParams = {"id"})
public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView t = new TextView(this);
        t.setTextColor(Color.BLACK);
        t.setText("product detail id = " + getIntent().getStringExtra("id"));
        setContentView(t);
    }
}

```
####跳转到目标Activity
```java
//通过url传递参数
Routers.getRouter(context,"schema://product/detail?id=123").open()

//通过withParam()传递
Routers.getRouter(context,"schema://product/detail").withParam("id","123").open()


```

### 为某些Activity配置拦截器
####简单的拦截
某些Activity可能需要登陆的用户才能跳转，所以跳转之前应该先判断该用户有没有登陆，如果没有登陆先跳到登陆界面，登陆之后在跳转到目标页    

```java

@ActivityMapping(value = {"product/buy"})
@ActivityInterceptor({LoginInterceptor.class})
public class ProductBuyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView t = new TextView(this);
        t.setTextColor(Color.BLACK);
        t.setText("product buy");
        setContentView(t);
    }
}

```

```java

public class LoginInterceptor implements Interceptor {

    boolean hasLogin;

    @Override
    public void doIntercept(Context context, String url, InterceptorChain interceptorChain) {
        if (hasLogin) {
            interceptorChain.doIntercept(context, url, interceptorChain);
        } else {
            interceptorChain.sendMsg(new RouterMsg(Router.MSG_FORWARD, "schema://user/login"));
        }
    }
}



```
####进阶
doIntercept方法是在主线程中调用的，所以如果存在耗时的操作或者需要访问网络还需要放到子线程中，在我们项目当中就存在这样一种业务需求，在购买任何产品之前都需要先请求网络判断一下当前用户是否开户绑卡，使用Intercepter就可以很好的将这块业务逻辑独立出来，而不需要在每个产品的购买界面都去写相同的代码,do not repeat yourselft!      

```java
@ActivityMapping(value = {"product/buy"})
@ActivityInterceptor({LoginInterceptor.class,CheckUserInfoInterceptor.class})
public class ProductBuyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView t = new TextView(this);
        t.setTextColor(Color.BLACK);
        t.setText("product buy");
        setContentView(t);
    }
}
```

```java
public class CheckUserInfoInterceptor implements Interceptor {


    GankApi gankApi;

    ProgressDialog dialog;

    public BrowserInterceptor() {
        gankApi = APIServiceFactory.createAPIService(TechHost.GankIO, GankApi.class);
    }

    @Override
    public void doIntercept(final Context context, final String s, final InterceptorChain interceptorChain) {
        dialog = new ProgressDialog(context);
        dialog.setCancelable(true);
        gankApi.getData("Android", 10, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GankIOModel<List<Dynamic>>>() {
                    @Override
                    public void onStart() {
                        dialog.show();
                    }

                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNext(GankIOModel<List<Dynamic>> listGankIOModel) {
                        interceptorChain.doIntercept(context, s, interceptorChain);
                    }
                });
    }
}

```
