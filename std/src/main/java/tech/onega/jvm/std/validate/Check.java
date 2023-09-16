package tech.onega.jvm.std.validate;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.lang.Lambda;

final public class Check {

  final public static class InvalidException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    InvalidException(String message, Object... args) {
      super(message == null ? "Null error message" : String.format(message, args));
    }

  }

  private static Validator VALIDATOR = Validation
    .buildDefaultValidatorFactory()
    .getValidator();

  public static InvalidException createError(String otherwise, Object... otherwiseArgs) {
    return new InvalidException(otherwise, otherwiseArgs);
  }

  public static void equals(Object v1, Object v2) throws InvalidException {
    equals(v1, v2, "Values:[\n  %s\n  !=  \n  %s\n]", v1, v2);
  }

  public static void equals(
    Object v1,
    Object v2,
    String otherwise,
    Object... otherwiseArgs) throws InvalidException {
    //
    if (Equals.no(v1, v2)) {
      fail(otherwise, otherwiseArgs);
    }
  }

  public static void fail(String otherwise, Object... otherwiseArgs) throws InvalidException {
    throw new InvalidException(otherwise, otherwiseArgs);
  }

  public static void isFalse(boolean value) throws InvalidException {
    isFalse(value, "Value != false");
  }

  public static void isFalse(boolean value, String otherwise, Object... otherwiseArgs) throws InvalidException {
    if (value) {
      fail(otherwise, otherwiseArgs);
    }
  }

  public static void isNull(Object value) throws InvalidException {
    isNull(value, "%s != null", value);
  }

  public static void isNull(Object value, String otherwise, Object... otherwiseArgs) throws InvalidException {
    if (value != null) {
      fail(otherwise, otherwiseArgs);
    }
  }

  public static void isNull(Object value, Supplier<String> errorFactory) throws InvalidException {
    isNull(value, errorFactory.get());
  }

  public static void isTrue(boolean value) throws InvalidException {
    isTrue(value, "Value != true");
  }

  public static void isTrue(boolean value, String otherwise, Object... otherwiseArgs) throws InvalidException {
    if (!value) {
      fail(otherwise, otherwiseArgs);
    }
  }

  public static void isTrue(boolean value, Supplier<String> errorFactory) throws InvalidException {
    isTrue(value, errorFactory.get());
  }

  public static void notBlank(@Nullable String value) throws InvalidException {
    notBlank(value, "Value is blank");
  }

  public static void notBlank(@Nullable String value, String otherwise, Object... otherwiseArgs) throws InvalidException {
    if ((value == null) || value.isBlank()) {
      fail(otherwise, otherwiseArgs);
    }
  }

  public static void notEmpty(@Nullable Collection<?> vals, String otherwise, Object... otherwiseArgs) throws InvalidException {
    if ((vals == null) || (vals.size() <= 0)) {
      fail(otherwise, otherwiseArgs);
    }
  }

  public static void notEquals(Object v1, Object v2) throws InvalidException {
    notEquals(v1, v2, "Values are equals:\n  %s\n  %s", v1, v2);
  }

  public static void notEquals(Object v1, Object v2, String otherwise, Object... otherwiseArgs) throws InvalidException {
    if (Equals.yes(v1, v2)) {
      fail(otherwise, otherwiseArgs);
    }
  }

  public static void notNull(Object value) throws InvalidException {
    notNull(value, "Value == null");
  }

  public static void notNull(Object value, String otherwise, Object... otherwiseArgs) throws InvalidException {
    if (value == null) {
      fail(otherwise, otherwiseArgs);
    }
  }

  public static void valid(Object value) throws InvalidException {
    valid(value, "Bean is not valid");
  }

  public static void valid(Object value, String otherwise, Object... otherwiseArgs) throws InvalidException {
    if (value == null) {
      fail("Value can't be null. " + otherwise, otherwiseArgs);
    }
    var errors = new HashSet<ConstraintViolation<Object>>();
    if (value instanceof Iterable) {
      var iterator = ((Iterable<?>) value).iterator();
      while (iterator.hasNext()) {
        errors.addAll(VALIDATOR.validate(iterator.next()));
        if (!errors.isEmpty()) {
          break;
        }
      }
    }
    else {
      errors.addAll(VALIDATOR.validate(value));
    }
    if (!errors.isEmpty()) {
      var validateErrors = errors.stream()
        .map(v -> v.toString())
        .collect(Collectors.joining("\n"));
      var otherwiseMessage = String.format(otherwise, otherwiseArgs);
      fail("%s \n %s", otherwiseMessage, validateErrors);
    }
  }

  public static Validator validator() {
    return VALIDATOR;
  }

  public static <E extends Throwable> void withThrow(Lambda.Void<E> lambda) throws InvalidException {
    withThrow(lambda, "No throw");
  }

  public static <E extends Throwable> void withThrow(Lambda.Void<E> lambda, String otherwise, Object... otherwiseArgs) throws InvalidException {
    try {
      lambda.invoke();
      fail(otherwise, otherwiseArgs);
    }
    catch (Throwable e) {
      //ignore
    }
  }

  public static <E extends Throwable> void withThrowType(Class<E> throwType, Lambda.Void<E> lambda) throws InvalidException {
    withThrowType(throwType, lambda, "No throw, or wrong throw type");
  }

  public static <E extends Throwable> void withThrowType(Class<E> throwType, Lambda.Void<E> lambda, String otherwise, Object... otherwiseArgs) throws InvalidException {
    try {
      lambda.invoke();
      fail(otherwise, otherwiseArgs);
    }
    catch (Throwable e) {
      if (!throwType.isInstance(e)) {
        fail(otherwise, otherwiseArgs);
      }
    }
  }

  public static <E extends Throwable> void withThrowValue(E throwValue, Lambda.Void<E> lambda) throws InvalidException {
    withThrowValue(throwValue, lambda, "No throw, or wrong throw are not equals");
  }

  public static <E extends Throwable> void withThrowValue(E throwValue, Lambda.Void<E> lambda, String otherwise, Object... otherwiseArgs) throws InvalidException {
    try {
      lambda.invoke();
      fail(otherwise, otherwiseArgs);
    }
    catch (Throwable e) {
      if (!throwValue.equals(e)) {
        fail(otherwise, otherwiseArgs);
      }
    }
  }

}
