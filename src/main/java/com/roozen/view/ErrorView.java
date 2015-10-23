package com.roozen.view;

import com.roozen.services.PropertiesService;

import static j2html.TagCreator.*;

public class ErrorView implements View {

    private String errorMessage;

    public ErrorView(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String render() {

        return body().with(
                h1("Could Not Get to Philosophy"),
                div().with(
                        h2(errorMessage),
                        a().withHref(PropertiesService.getInitialPage()).withText("Back to Start")
                        )
                ).render();
    }
}
