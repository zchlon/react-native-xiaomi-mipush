import {
  Platform,
  NativeModules ,
  DeviceEventEmitter
} from 'react-native';

const { XiaomiPush } = NativeModules;

const listeners = {}
const receiveRegIdEvent = 'getRegistrationId'
const receiveCustomMsgEvent = 'receivePushMsg'
const receiveNotificationEvent = 'receiveNotification'
const openNotificationEvent = 'openNotification'

export default class XMPush {
  /**
   * 初始化推送服务
   */
  static registerPush() {

    if (Platform.OS === 'android') {
      XiaomiPush.registerPush()
        .then(res => console.log(res));
    }

  }

  /**
     * 关闭推送
     */
  static unregisterPush() {

    if (Platform.OS === 'android') {
      XiaomiPush.unregisterPush();
    }

  }

  /**
     * 设置别名
     */
  static setAlias(alias) {

    if (Platform.OS === 'android') {
      XiaomiPush.setAlias(alias);
    }

  }

  /**
   * 清空指定别名
   */
  static unsetAlias(alias) {

    if (Platform.OS === 'android') {
      XiaomiPush.unsetAlias(alias);
    }

  }

  /**
     * RegId下发事件
     * @param {Function} cb = (Object）=> {}
     */
  static addReceiveRegIdListener(cb) {

    if (Platform.OS === 'android') {

      listeners[receiveRegIdEvent] = DeviceEventEmitter.addListener(
        receiveRegIdEvent,
        args => cb(args)
      )

    }

  }

  /**
   * 移除RegId下发事件监听
   * @param {Function} cb = (Object）=> {}
   */
  static removeReceiveRegIdListener() {

    if (!listeners[receiveRegIdEvent]) {
      return;
    }

    listeners[receiveRegIdEvent].remove();
    listeners[receiveRegIdEvent] = null;

  }


  /**
   * 监听：接收推送事件
   * @param {Function} cb = (Object）=> {}
   */
  static addReceiveNotificationListener(cb) {
    listeners[cb] = DeviceEventEmitter.addListener(
      receiveNotificationEvent,
      map => {
        cb(map)
      }
    )
  }

  /**
   * 监听：点击推送事件
   * @param {Function} cb  = (Object）=> {}
   */
  static addOpenNotificationListener(cb) {
    listeners[cb] = DeviceEventEmitter.addListener(
      openNotificationEvent,
      message => {
        cb(message)
      }
    )
  }

};
