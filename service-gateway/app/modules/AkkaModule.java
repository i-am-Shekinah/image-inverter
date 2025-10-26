package modules;

import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;
import actors.ImageInverterActor;

public class AkkaModule extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bindActor(ImageInverterActor.class, "image-inverter-actor");
    }
}
