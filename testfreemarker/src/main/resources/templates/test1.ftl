<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<br>
遍历list集合中的数据，list存入的模型列表为(stus)
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>出生日期</td>
        <td>朋友</td>
    </tr>
        <#--<#if stu??>-->
            <#list stus as stu>
                 <tr>
                     <td>${stu_index+1}</td>
                     <td <#if stu.name=="小红">style="background:red;"</#if>>${stu.name}</td>
                     <td>${stu.age}</td>
                     <td>${stu.birthday?string("YYYY年MM月dd日")}</td>
                     <td>
                         <#if stu.friends??>
                            <#list stu.friends as friend>
                                ${friend.name!''}
                            </#list>
                         </#if>
                     </td>
                 </tr>
            </#list>
        <#--</#if>-->
</table>
<br>
<br>
遍历map集合中的数据
<br>
<#--遍历map集合中的数据一共有三种方式可以获取
    1、通过存入域中的map['具体的那个Key'].属性
    2、通过key获取具体map的key实现，例:Map.stus.name
    3、遍历map的key来进行实现   使用list标签，第一种方式类型，唯一不同的是拿取道德是Map中的所有Key
        list map?keys as key
        ${map[key].name}
    4、判空使用if或者缺省默认!""
-->
姓名:${stuMap["stu1"].name!''}
年龄:${stuMap["stu1"].age!''}
<br>
姓名:${stuMap.stu1.name!''}
年龄:${stuMap.stu1.age!''}
<br>
<#-- 使用list标签遍历map的keys-->
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>出生日期</td>
    </tr>
    <#list stuMap?keys as key>
    <tr>
        <td>${key_index+1}</td>
        <td>${stuMap[key].name!''}</td>
        <td>${stuMap[key].age!''}</td>
        <td>${stuMap[key].birthday?string("YYYY年MM月dd日")}</td>
    </tr>
    </#list>
</table>

<#--查看一个集合中的数据长度size-->
map集合的个数:${stuMap?size}
<br>
<#--内建函数C将整数转换为字符串输出-->
${point?c}
<br>
<#--将对象转换为json字符串-->
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />

开户行:${data.bank} 账户:${data.account}

</body>
</html>