import org.mineskin.JsoupRequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.Skin;

public class Test {

    public static void main(String[] args) {
        MineSkinClient client = MineSkinClient.builder()
                .requestHandler(JsoupRequestHandler::new)
                .userAgent("mCore/v1.0")
                .apiKey("94791932570333c4675c3ffcff865de72cb41830b3090d15f96c608da8ce70ae")
                .build();
        client.skins().get("3f3f8a24f53745b59b68d567c28fac1f").thenAccept(response -> {
            Skin skin = response.getSkin();

            System.out.println(skin.texture().data().value());
            System.out.println(skin.texture().data().signature());
        });
    }

}
