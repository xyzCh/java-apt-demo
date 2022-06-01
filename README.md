## java-apt-demo

此demo是为了验证:  
使用apt在编译时对字段添加`lombok.Getter(onMethod__={@CustomAnnotation(${fieldName})})`注解([See](https://projectlombok.org/features/experimental/onX)),  
实现通过读取getter方法的CustomAnnotation获取相应fieldName的可行性.  
  
> 以下将自定义的annotation processor称为custom processor, lombok的annotation processor称为lombok processor

run之前需要替换Main.java中的${lombok path}, 之后可以看到ide控制台输出:
![image](https://user-images.githubusercontent.com/28343843/171083260-34024ea6-072e-4075-907b-41fb89ea2b58.png)  
第一行输出可以看出此时ide自动编译后的User.class中是不存在getter方法的, 下边输出看出custom processor已经把注解添加到了字  
段上, 此时使用javap查看run之后经过processor处理过的User.class
![image](https://user-images.githubusercontent.com/28343843/171084500-4df0cd57-c189-46a5-8e4b-8f7e00b6989b.png)  
可以看到生成了getter方法, 且存在自定义的注解, bingo!

---
后续思考:  
当processor之间依赖对方的处理结果或对方的后续处理时该如何处理? 在此demo, 通过在Main.java中设置javac参数
```java
...
"-processor",
"com.example.AnnoProc,lombok.launch.AnnotationProcessorHider$AnnotationProcesso",
...
```
使lombok processor在custom processor之后执行, 如果lombok processor先执行将会编译失败(custom processor添加的@Getter  
注解将不会被lombok processor处理, custom processor依赖于lombok processor的后续处理)  
![image](https://user-images.githubusercontent.com/28343843/171102798-bff42195-e98b-4918-8690-bb27b3f0a941.png)  
把custom processor打包为jar之后通过java SPI加载时顺序是如何的? 通过查看java源码, processor加载顺序依赖于classpath顺序,  
因此只要保证编译时classpath顺序和期待的processor执行顺序一致即可.



