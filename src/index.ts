import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-strongly-log' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const SLog = NativeModules.StronglyLogModule
  ? NativeModules.StronglyLogModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

/**
 * StronglyLog 是一个日志工具类，提供静态方法进行日志记录和管理
 *
 * @remarks
 * 该类禁止实例化，所有方法均为静态方法，直接通过类名调用
 *
 * @example
 * ```typescript
 * StronglyLog.info('这是一条信息日志');
 * StronglyLog.error('错误信息');
 * ```
 *
 * @public
 */
export default class StronglyLog {
  // Constructor is intentionally private to prevent instantiation of this utility class
  private constructor() {
    throw new Error(
      'StronglyLog cannot be instantiated. Use static methods instead.'
    );
  }
  /**
   * 静态方法，用于记录信息级别的日志
   * @param s 要记录的日志信息
   */
  static info(s: string) {
    SLog.info(s);
  }
  /**
   * 静态调试方法，用于输出调试级别的日志信息
   * @param s 要记录的调试信息字符串
   */
  static debug(s: string) {
    SLog.debug(s);
  }
  /**
   * 静态方法，用于输出警告级别的日志信息
   * @param s 要记录的警告信息字符串
   */
  static warn(s: string) {
    SLog.warn(s);
  }
  /**
   * 静态方法，用于记录错误级别的日志
   * @param s 要记录的错误信息
   */
  static error(s: string) {
    SLog.error(s);
  }
  /**
   * 清除所有日志记录
   * @returns Promise<number> 默认返回1
   */
  static clearAllLogs(): Promise<number> {
    return SLog.clearAllLogs();
  }
  /**
   * 清除4天前的日志
   * @returns Promise<number> 默认返回1
   */
  static clearOldLogs(): Promise<number> {
    return SLog.clearOldLogs();
  }
  /**
   * 压缩日志文件 返回为压缩后的文件路径字符串
   * @returns 返回一个Promise
   */
  static zipLogFiles(fileName: string): Promise<string> {
    return SLog.zipLogFiles(fileName);
  }
  /**
   * 强制刷新日志缓冲区
   * @returns 返回一个 Promise
   */
  static flush(): Promise<number> {
    return SLog.flush();
  }
}
