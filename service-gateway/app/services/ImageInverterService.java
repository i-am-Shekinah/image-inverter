package services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import scala.concurrent.Future;
import scala.compat.java8.FutureConverters;

import java.io.File;
import java.util.concurrent.CompletionStage;

import actors.ImageInverterActor;

public class ImageInverterService {

    private final ActorSystem actorSystem;
    private final ActorRef inverterActor;

    public ImageInverterService(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
        this.inverterActor = actorSystem.actorOf(ImageInverterActor.props(), "imageInverterActor");
    }

    public CompletionStage<Object> invertImage(File inputFile, File outputFile) {
        ImageInverterActor.InvertImageMessage msg = new ImageInverterActor.InvertImageMessage(inputFile, outputFile);
        Future<Object> future = Patterns.ask(inverterActor, msg, 5000);
        return FutureConverters.toJava(future);
    }
}
