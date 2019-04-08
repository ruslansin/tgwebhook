# TgWebhook [![](https://jitpack.io/v/snqlby/tgwebhook.svg)](https://jitpack.io/#snqlby/tgwebhook)

## Description
This library contains an implementation to process Webhook requests for 
[rubenlagus/TelegramBots](https://github.com/rubenlagus/TelegramBots).

It provides simple ways to process incoming requests.
Code reduction is achieved through the use of annotations to configure accepted requests.

```Java
@AcceptTypes({UpdateType.MESSAGE})
@Component
public class StartHandler {

  @CommandMethod(value = "/start", locality = Locality.PRIVATE)
  public BotApiMethod onStartCommand(AbsSender bot, Message message, List<String> args) {
    return new SendMessage()
      .setText("Hello world")
      .setChatId(message.getChatId());
  }

}
```
## Usage
*JitPack*: see [here](https://jitpack.io/#snqlby/tgwebhook/v1.4.0)

## Bot examples
GuardBot: https://github.com/snqlby/guardbot

## Initialization
### Java Spring
If you are using Spring for your application, try the following way:

```Java
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    
    final Map<String, Object> handlers = context.getBeansWithAnnotation(AcceptTypes.class);
    handlers.values().forEach(handler -> {
      Handlers.addHandler(handler);
    });
  }

}
```

```Java
  /*
   Your code with 
   1. TelegramWebhookBot bot implementation
   2. var resolver = new RequestResolver(bot);
  */
  public BotApiMethod<?> yourMethodName(Update update) {
    return resolver.handleRequest(update);
  }
```


Supported annotations:
- @InlineMethod
- @CallbackMethod(data, game_short_name, locality, origin)
- @ChannelPostMethod
- @JoinMethod(room, reason)
- @LeaveMethod(room, reason)
- @EditedChannelPostMethod
- @MessageMethod(locality,flag)
- @EditedMessageMethod
- @CommandMethod(command, locality)
- @PreCheckoutMethod
- @ShippingMethod


