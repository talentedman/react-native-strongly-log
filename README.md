# react-native-strongly-log

React Native Log for Android and iOS

## Installation

```sh
npm install react-native-strongly-log
yarn add react-native-strongly-log
pnpm i react-native-strongly-log
```

## Usage


```js
import StronglyLog from 'react-native-strongly-log';

// ...
记录日志
StronglyLog.info('Hello, World!');
StronglyLog.debug('Hello, World!');
StronglyLog.warn('Hello, World!');
StronglyLog.error('Hello, World!');

清除所有日志记录
await StronglyLog.clearAllLogs()

清除4天前的日志
await StronglyLog.clearOldLogs()

压缩日志文件 返回为压缩后的文件路径字符串
await StronglyLog.zipLogFiles(fileName)

flush日志
await StronglyLog.flush()
```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
