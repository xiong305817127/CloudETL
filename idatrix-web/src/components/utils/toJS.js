/**
 * By converting Immutable.JS objects to plain JavaScript values within a HOC, 
 * we achieve Dumb Component portability, but without the performance hits of 
 * using toJS() in the Smart Component.
 * Note: if your app requires high performance, you may need to avoid toJS() 
 * altogether, and so will have to use Immutable.JS in your dumb components. 
 * However, for most apps this will not be the case, and the benefits of keeping
 *  Immutable.JS out of your dumb components (maintainability, portability and 
 * easier testing) will far outweigh any perceived performance improvements of 
 * keeping it in.
 * 
 * In addition, using toJS in a Higher Order Component should not cause much, 
 * if any, performance degradation, as the component will only be called when 
 * the connected component’s props change. As with any performance issue, 
 * conduct performance checks first before deciding what to optimize.
 */

 
import React from 'react';
import { Iterable } from 'immutable';

export default  (WrappedComponent) => wrappedComponentProps => {
  const KEY = 0
  const VALUE = 1

  const propsJS = Object.entries(
    wrappedComponentProps
  ).reduce((newProps, wrappedComponentProp) => {
    newProps[wrappedComponentProp[KEY]] = Iterable.isIterable(
      wrappedComponentProp[VALUE]
    )
      ? wrappedComponentProp[VALUE].toJS()
      : wrappedComponentProp[VALUE]
    return newProps
  }, {})

  return <WrappedComponent {...propsJS} />
}