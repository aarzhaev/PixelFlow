/**
 * 
 * PixelFlow | Copyright (C) 2016 Thomas Diewald - http://thomasdiewald.com
 * 
 * A Processing/Java library for high performance GPU-Computing (GLSL).
 * MIT License: https://opensource.org/licenses/MIT
 * 
 */

package OpticalFlow_Basic;



import processing.core.*;
import processing.opengl.PGraphics2D;
import src.OpticalFlow;
import src.PixelFlow;

public class Main_OpticalFlow_Basic extends PApplet {
 

  OpticalFlow opticalflow;
  PGraphics2D pg_oflow;
  PGraphics2D pg_src;
  
  public void settings() {
    size(1000, 800, P2D);
    smooth(4);
  }

  public void setup() {

    // main library context
    PixelFlow context = new PixelFlow(this);
    context.print();
    context.printGL();
    
    // opticalflow
    opticalflow = new OpticalFlow(context, width, height);
    
    // some flow parameters
    opticalflow.param.flow_scale         = 100;
    opticalflow.param.temporal_smoothing = 0.1f;
    opticalflow.param.display_mode       = 0;
    
    // render target
    pg_oflow = (PGraphics2D) createGraphics(width, height, P2D);
    pg_oflow.smooth(4);
    
    // drawing canvas, used as input for the optical flow
    pg_src = (PGraphics2D) createGraphics(width, height, P2D);
    pg_src.smooth(4);
  
    frameRate(60);
//    frameRate(1000);
  }
  

  // animated rectangle data
  float rs = 80;
  float rx = 100;
  float ry = 100;
  float dx = 3;
  float dy = 2.4f;
  
  public void draw() {

    // update rectangle position
    rx += dx;
    ry += dy;
    // keep inside viewport
    if(rx <        rs/2) {rx =        rs/2; dx = -dx; }
    if(rx > width -rs/2) {rx = width -rs/2; dx = -dx; }
    if(ry <        rs/2) {ry =        rs/2; dy = -dy; }
    if(ry > height-rs/2) {ry = height-rs/2; dy = -dy; }
    
    // update input image
    pg_src.beginDraw();
    pg_src.clear();
    pg_src.background(0);
    
    pg_src.rectMode(CENTER);
    pg_src.fill(150, 200, 255);
    pg_src.rect(rx, ry, rs, rs, rs/3f);
    
    pg_src.fill(200, 150, 255);
    pg_src.noStroke();
    pg_src.ellipse(mouseX, mouseY, 100, 100);
    pg_src.endDraw();
    
    
    // update Optical Flow
    opticalflow.update(pg_src);
    
    // render Optical Flow
    pg_oflow.beginDraw();
    pg_oflow.clear();
    pg_oflow.endDraw();
    
    // opticalflow visualizations
    if(mousePressed){
      opticalflow.renderVelocityShading(pg_oflow);
    }
    opticalflow.renderVelocityStreams(pg_oflow, 10);
    
    // display result
    background(0);
    image(pg_src, 0, 0);
    image(pg_oflow, 0, 0);
    
    // info
    String txt_fps = String.format(getClass().getName()+ "   [size %d/%d]   [frame %d]   [fps %6.2f]", pg_oflow.width, pg_oflow.height, opticalflow.UPDATE_STEP, frameRate);
    surface.setTitle(txt_fps);
  }
  

  public static void main(String args[]) {
    PApplet.main(new String[] { Main_OpticalFlow_Basic.class.getName() });
  }
}