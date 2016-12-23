 
统一Hyrid App开发过程中的跳转，通过注解为每个Activity标识一个URL,支持为一个Activity配置多个URL，支持通过URL传递参数，支持多Module跳转，支持为某些Activity配置拦截器。


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
