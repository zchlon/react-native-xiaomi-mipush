# react-native-xiaomi-mipush

本插件库还在调整中，暂支持Android

<br>

## 1. 安装 installation

`npm install react-native-xiaomi-mipush --save`

or

`yarn add react-native-xiaomi-mipush`


## 2. 配置

### 2.1 Android

* build.gradle
> android/app/build.gradle  设置申请的AppId和AppKey

```
android {
    defaultConfig {
        applicationId "yourApplicationId"           //你的应用包名
        ...
        manifestPlaceholders = [
                XIAOMI_APPID: "yourAppId",         //在此替换你的AppId
                XIAOMI_APPKEY: "yourAppKey"        //在此替换你的AppKey
        ]
    }
}
```

* AndroidManifest.xml
> android/app/src/main/AndroidManifest.xml

```
<meta-data
  android:name="XIAOMI_APPID"
  android:value="ID=${XIAOMI_APPID}" />
<meta-data
  android:name="XIAOMI_APPKEY"
  android:value="KEY=${XIAOMI_APPKEY}" />
```

## 使用

* 引入

```javascript
import XiaoMiPush from 'react-native-xiaomi-mipush';
```

* 初始化推送服务 必须调用

```
XiaoMiPush.registerPush();
```

* 关闭推送服务

```
XiaoMiPush.unregisterPush();
```

* 获取RegId

```
MiPush.getRegId().then(regId => {
  console.log(regId)
});
```

* 设置别名

```
XiaoMiPush.setAlias('xxxx');
```

* 清空指定别名

```
XiaoMiPush.unsetAlias('xxxx');
```
