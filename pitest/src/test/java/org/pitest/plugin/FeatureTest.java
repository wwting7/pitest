package org.pitest.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class FeatureTest {

  @Test
  public void shouldObeyHashcodeEqualsContract() {
    EqualsVerifier.forClass(Feature.class)
            .withOnlyTheseFields("name")
            .verify();
  }

  @Test
  public void shouldUseOnlyNameForEquality() {
    assertThat(Feature.named("foo"))
    .isEqualTo(Feature.named("foo").withDescription("?").withOnByDefault(false));
  }

  @Test
  public void nameEqualityIsCaseInsensitive() {
    assertThat(Feature.named("foo"))
            .isEqualTo(Feature.named("FOO"));
  }

}
