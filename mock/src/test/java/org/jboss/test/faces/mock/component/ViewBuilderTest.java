package org.jboss.test.faces.mock.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.jboss.test.faces.mock.FacesMock;
import org.junit.Test;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UIViewRoot;

public class ViewBuilderTest {

    private static final String FOO_XML = "/foo.xml";

    @Test
    public void testCreateView() {
        UIViewRoot component = ViewBuilder.createView().setViewId(FOO_XML).getComponent();
        assertTrue(component instanceof UIViewRoot);
        checkViewId(component);
    }

    private void checkViewId(UIViewRoot component) {
        FacesMock.replay(component);
        assertEquals(FOO_XML, component.getViewId());
        FacesMock.verify(component);
    }

    @Test
    public void testCreateViewUIViewRoot() {
        UIViewRoot viewRoot = FacesMock.createMock(UIViewRoot.class);
        ViewBuilder viewBuilder = ViewBuilder.createView(viewRoot).setViewId(FOO_XML);
        assertSame(viewRoot,viewBuilder.getComponent());
        checkViewId(viewRoot);
    }

    @Test
    public void testBuildView() throws Exception {
        ViewBuilder viewBuilder = ViewBuilder.createView().children(
                    ViewBuilder.component().id("foo"),
                    ViewBuilder.component(UIInput.class).id("input")
                  ).facets(
                    ViewBuilder.facet("header")
                  ).setViewId(FOO_XML);
        viewBuilder.replay();
        UIViewRoot viewRoot = viewBuilder.getComponent();
        assertEquals(2, viewRoot.getChildCount());
        assertEquals(1, viewRoot.getFacetCount());
        UIComponent facet = viewRoot.getFacet("header");
        assertNotNull(facet);
        assertSame(viewRoot,facet.getParent());
        assertSame(viewRoot,viewRoot.getChildren().get(1).getParent());
        viewBuilder.verify();
    }
}
