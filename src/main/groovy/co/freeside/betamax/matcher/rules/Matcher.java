package co.freeside.betamax.matcher.rules;

import co.freeside.betamax.message.Request;

public interface Matcher {
  int compare(Request a, Request b);
}
