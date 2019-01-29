/**
 *  This file contains a list of {@link com.crashinvaders.vfx.PostProcessorFilter}
 *  class implementations that are ready to be used with the new refactored
 *  {@link com.crashinvaders.vfx.PostProcessor}.
 *  They all are mostly supported, but will likely work wrong with resize
 *  (generated empty #resize(int, int) method should be implemented).
 *
 *  [UP TO DATE]
 *  Blur
 *  Combine
 *  Convolve1D
 *  Convolve2D
 *  Copy
 *  MotionFilter
 *  Threshold
 *  RadialDistortion
 *  LensFlare
 *  LensFlare2
 *
 *  [OUTDATED / NOT TESTED]
 *  Bias
 *  CameraBlur
 *  FxaaFilter
 *  MultipassFilter
 *  NfaaFilter
 *  RadialBlur
 *  Vignetting
 *  Zoom
 */
package com.crashinvaders.vfx.filters;