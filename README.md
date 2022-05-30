### java-apt-demo

此demo是为了验证:  
使用apt在编译时对字段添加`lombok.Getter(onMethod__={@CustomAnnotation(${FieldName})})`注解([See](https://projectlombok.org/features/experimental/onX)),  
实现通过读取getter方法的CustomAnnotation获取相应FieldName的可行性.  
  
  
运行前需要替换一下Main.java中的${lombok path}
