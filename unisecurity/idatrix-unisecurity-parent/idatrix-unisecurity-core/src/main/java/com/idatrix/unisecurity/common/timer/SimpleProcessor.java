package com.idatrix.unisecurity.common.timer;

import org.springframework.stereotype.Service;

import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by james on 2017/8/1.
 */
@Service
public class SimpleProcessor implements Processor {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public Set<String> getSupportedOptions() {
        return null;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return null;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return null;
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {

    }

    public void process() {
        System.out.println("processing next 10 at " + new Date());
        for (int i = 0; i < 10; i++) {
            System.out.println("processing " + counter.incrementAndGet());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return null;
    }
}
