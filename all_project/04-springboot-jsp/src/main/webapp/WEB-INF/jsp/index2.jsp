<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<div class="sm">
    <form method="POST" enctype="multipart/form-data" action="/web/upload">
        <h1 style="color:green;">这是个jsp页面，如果你看到这个页面，说明成功了！</h1>
        <p>文件：<input type="file" name="file"/></p>
        <p><input type="submit" value="上传" /></p>
    </form>
</div>