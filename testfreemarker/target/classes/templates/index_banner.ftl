<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="http://localhost:80/plugins/normalize-css/normalize.css" />
    <link rel="stylesheet" href="http://localhost:80/plugins/bootstrap/dist/css/bootstrap.css" />
    <link rel="stylesheet" href="http://localhost:80/css/page-learing-index.css" />
    <link rel="stylesheet" href="http://localhost:80/css/page-header.css" />
</head>
<body>
<div class="banner-roll">
    <div class="banner-item">
        <#--<div class="item" style="background-image: url(http://localhost:80/img/widget-bannerB.jpg);"></div>-->
        <#--<div class="item" style="background-image: url(http://localhost:80/img/widget-bannerA.jpg);"></div>-->
        <#--<div class="item" style="background-image: url(http://localhost:80/img/widget-banner3.png);"></div>-->
        <#--<div class="item" style="background-image: url(http://localhost:80/img/widget-bannerB.jpg);"></div>-->
        <#--<div class="item" style="background-image: url(http://localhost:80/img/widget-bannerA.jpg);"></div>-->
        <#--<div class="item" style="background-image: url(http://localhost:80/img/widget-banner3.png);"></div>-->
        <#--非空判断-->
        <#if model??>
            <#--遍历model中的value-->
            <#list model as item>
                <div class="item" style="background-image: url(${item.value});"></div>
            </#list>
        </#if>

    </div>
    <div class="indicators"></div>
</div>
<script type="text/javascript" src="http://localhost:80/plugins/jquery/dist/jquery.js"></script>
<script type="text/javascript" src="http://localhost:80/plugins/bootstrap/dist/js/bootstrap.js"></script>
<script type="text/javascript">
    var tg = $('.banner-item .item');
    var num = 0;
    for (i = 0; i < tg.length; i++) {
        $('.indicators').append('<span></span>');
        $('.indicators').find('span').eq(num).addClass('active');
    }

    function roll() {
        tg.eq(num).animate({
            'opacity': '1',
            'z-index': num
        }, 1000).siblings().animate({
            'opacity': '0',
            'z-index': 0
        }, 1000);
        $('.indicators').find('span').eq(num).addClass('active').siblings().removeClass('active');
        if (num >= tg.length - 1) {
            num = 0;
        } else {
            num++;
        }
    }
    $('.indicators').find('span').click(function() {
        num = $(this).index();
        roll();
    });
    var timer = setInterval(roll, 3000);
    $('.banner-item').mouseover(function() {
        clearInterval(timer)
    });
    $('.banner-item').mouseout(function() {
        timer = setInterval(roll, 3000)
    });
</script>
</body>
</html>