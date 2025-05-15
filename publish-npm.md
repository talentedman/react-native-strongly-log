```
创建 NPM 账号: 如果你还没有，去 npmjs.com 注册一个账号。
登录 NPM CLI: npm login

检查 package.json 和 .npmignore:
确保 package.json 中的 version 是你想要发布的版本。
使用 .npmignore 文件（如果 files 字段不够用）来排除不需要发布的文件。.npmignore 的语法和 .gitignore 相同。通常，files 字段是更推荐的方式。

构建你的库: 确保运行了构建脚本。如果 prepare 脚本配置正确，这一步会在 npm publish 时自动完成。
yarn build # 或者 npm run build

测试将要发布的内容 (Dry Run):
npm pack --dry-run
这会显示哪些文件将被包含在最终的 .tgz 包中，帮助你确认 files 或 .npmignore 配置是否正确。


发布到 NPM

版本号: 每次发布新版本前，务必更新 package.json 中的 version 字段。你可以手动修改，或者使用 npm version 命令：

npm version patch (例如 1.0.0 -> 1.0.1)
npm version minor (例如 1.0.1 -> 1.1.0)
npm version major (例如 1.1.0 -> 2.0.0) 这个命令会自动更新 package.json，并根据配置创建一个 git tag。

发布:
npm publish

如果你的包名是 scoped (例如 @username/lib-name)，并且是第一次发布，你可能需要添加 --access public 标志（如果你想让它成为公开包）：

npm publish --access public
```