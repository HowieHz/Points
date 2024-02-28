# Points

![GitHub](https://img.shields.io/github/license/HowieHz/Points)
![GitHub all releases](https://img.shields.io/github/downloads/HowieHz/Points/total)
![GitHub release (latest by date)](https://img.shields.io/github/downloads/HowieHz/Points/latest/total)
![GitHub repo size](https://img.shields.io/github/repo-size/HowieHz/Points)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/6514d15bcda04c4db6ef136204282a46)](https://www.codacy.com/gh/HowieHz/Points/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=HowieHz/Points&amp;utm_campaign=Badge_Grade)
[![DeepSource](https://deepsource.io/gh/HowieHz/Points.svg/?label=active+issues&show_trend=true&token=BsdaFuwaOvvqaAa8w-r7YmmN)](https://deepsource.io/gh/HowieHz/Points/?ref=repository-badge)

![项目图标, 感谢@Lenc1](./img/P.png)

A paper plugin, some useful points

插件支持 1.16.5 1.17.1 1.18.2 1.19.x 1.20.x **Spigot, Paper及其衍生端** (如Purpur)

插件最低要求为 **java16**

## 主要功能

1. 高亮自身 公屏发坐标 (/here指令)
2. 得到某个玩家的坐标 (/where指令)
3. 死亡的时候发送死亡位置 (/death指令)
4. 记录死亡信息 (/death指令)
5. 随身开启末影箱 (/enderchest指令)
6. 防爆

   ### 特殊需求功能

7. 公平PVP模式 (/fair-pvp指令, 关闭AureliumSkills插件的属性加成, 需要PlaceholderAPI和AureliumSkills插件)

**其他**

1. 高可配置性，各个模块都可以单独关闭
2. 完全可配置的语言文件
3. 开箱即用 -> 默认配置文件:
    1. 作用于自身的指令无需权限
    2. 作用于他人的指令（除*where*指令）和*points reload*指令需要权限
    3. op 默认有*points reload*使用权限
4. tab补全根据 模块是否开启 和 是否有权限 自动隐藏

   有权限的玩家看到的
   ![tab-complete-control1](./img/tab-complete-control1.png)
   
   无权限玩家看到的
   ![tab-complete-control2](./img/tab-complete-control2.png)

5. 权限支持 * 通配符

## 功能介绍

### 防爆 anti-boom

(对于床和重生锚来说是阻止使用)

* 床 -> bed
* 苦力怕 -> creeper
* 末影水晶 -> ender-crystal
* 恶魂 -> ghast
* tnt矿车 -> minecart-tnt
* 重生锚 -> respawn-anchor
* TNT -> tnt
* 凋零 -> wither

![anti-boom](./img/antiboom-listener1.png)
![anti-boom](./img/antiboom-listener2.png)

### 指令和权限枚举

#### /here (/he)

    高亮自身 公屏发坐标
    points.command.here

![here](./img/here-command-demo.png)

#### /where (/whe, /wh)

    获取自己坐标
    points.command.where.self
    
    /where Shacha086
    获取玩家坐标
    points.command.where.other
    points.command.where.other.Shacha086 //对于特定玩家

#### /death (/de)

    /death message
    切换是否发送死亡坐标
    points.command.death.message

    是否发送死亡坐标 监听器管理
    points.listener.death.message

![death-message](./img/death-message-demo.png)

    /death log
    获取自己的死亡历史
    points.command.death.log.self

    /death log Shacha086
    获取玩家死亡历史
    points.command.death.log.other
    points.command.death.log.other.Shacha086 //对于特定玩家

![death-log](./img/death-log-demo.png)

#### /enderchest (/ec)

    /enderchest
    开启自己的末影箱
    points.command.enderchest.self

    /enderchest Shacha086
    开启指定玩家末影箱 (要求该玩家在线)
    points.command.enderchest.other
    points.command.enderchest.other.Shacha086 //对于特定玩家

#### points主指令 (/po)

    /points reload
    重载插件
    points.command.reload

    /points help
    获取帮助
    points.command.help

#### /fair-pvp

    /fair-pvp on
    /fair-pvp off
    切换自己的pvp模式
    points.command.fair-pvp.self
    
    /fair-pvp on Shacha086
    /fair-pvp off Shacha086
    切换指定玩家的pvp模式 (要求该玩家在线)
    points.command.fair-pvp.other
    points.command.fair-pvp.Shacha086 //对于特定玩家

## 构建

spigot-api版本为1.16.5

最新开发版自动构建可在github action中下载

## 代码质量

Points使用 [sonarcloud.io](https://sonarcloud.io/project/overview?id=HowieHz_Points), [codacy.com](https://app.codacy.com/gh/HowieHz/Points/dashboard), [deepsource.io](https://deepsource.io/gh/HowieHz/Points) 监控代码质量

## bStats

[![bStats](https://bstats.org/signatures/bukkit/Points.svg)](https://bstats.org/plugin/bukkit/Points/16544)

## 项目状态

![Alt](https://repobeats.axiom.co/api/embed/4eb97438e77c1ee2b0343992a726e6ff81242b90.svg "Repobeats analytics image")

## 更新计划

1. 添加床和重生锚有爆炸伤害但不破坏地形的选项
2. 支持mysql等数据库存储
3. 配置文件占位符检查
4. 右键打开潜影箱
5. 白名单插件，邀请制白名单功能，可以通过指令刷新储存的uuid
6. 均衡饮食功能
7. Help指令 玩家只能看到有权限执行的指令的帮助
8. 语言文件的最大化，前缀的分离
9.  跟随版本自动更新的配置文件
10. 能自定义的常量都允许自定义
11. 在配置中允许用户调试启用的世界和禁用的世界
12. 调用paperlib进行优化
13. 兼容到1.8
14. 动态视距

### 反更新计划（即不太可能更新的，除非有强烈需求）

1. 在服务器隐身（玩家列表也看不到的那种可以用来抓挂）(多个插件中已经有这个功能)
