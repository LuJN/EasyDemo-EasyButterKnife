# EasyDemo-EasyButterKnife

造轮子系列-仿ButterKnife

- $T 是类型替换, 一般用于 ("$T foo", List.class) => List foo. $T 的好处在于 JavaPoet 会自动帮你补全文件开头的 import. 如果直接写 ("List foo") 虽然也能生成 List foo， 但是最终的 java 文件就不会自动帮你添加 import java.util.List.
- $L 是字面量替换, 比如 ("abc$L123", "FOO") => abcFOO123. 也就是直接替换.
- $S 是字符串替换, 比如: ("$S.length()", "foo") => "foo".length() 注意 $S 是将参数替换为了一个带双引号的字符串. 免去了手写 "\"foo\".length()" 中转义 (\") 的麻烦.
- $N 是名称替换, 比如你之前定义了一个函数 MethodSpec methodSpec = MethodSpec.methodBuilder("foo").build(); 现在你可以通过 $N 获取这个函数的名称 ("$N", methodSpec) => foo.
