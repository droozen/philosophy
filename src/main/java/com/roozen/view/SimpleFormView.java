package com.roozen.view;

import com.roozen.services.PropertiesService;

import static j2html.TagCreator.*;

public class SimpleFormView implements View {

    /**
     * Render a simple form with an input box for the URL and a link to view the results of past executions
     *
     * @return
     */
    @Override
    public String render() {
        return body().with(
                h1("Getting to Philosophy"),
                form().withMethod("post").withAction(PropertiesService.getSubmitPage())
                        .with(
                                label("Starting URL:"),
                                input().withType("text").withId("wiki-url").withName("wiki-url").isRequired(),
                                button("Submit").withType("submit")
                        ),
                div().with(a("View Historical Executions").withHref(PropertiesService.getExecutionPage()))
        ).render();
    }

}
