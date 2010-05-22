
image {
   resolution 800 600
   aa 0 1
   samples 1
}

camera {
   type pinhole
   eye 0.0 0.0 10.0
   target 0.0 0.0 0.0
   up 0.0 10.0 0.0
   fov 60 
   aspect 1.333
}


light {
   type point
   color { "sRGB nonlinear" 1.000 1.000 1.000 }
   power 40
   p 0.0 0.0 5.0
}

shader {
   name organic0
   type organic
   depth 3
}

shader {
   name wood0
   type wood
   depth 4
}

shader {
   name marble0
   type marble
   depth 5
}

shader {
   name water0
   type water
   depth 6
}

shader {
   name fire0
   type fire
   depth 7
}

shader {
   name stone0
   type stone
}

object {
   shader organic0
   type sphere
   name sphere0
   c 0 1.5 0
   r 1.5
}

object {
   shader stone0
   type sphere
   name sphere1
   c 0 -1.5 0
   r 1.5
}

object {
   shader water0
   type sphere
   name sphere2
   c -3 1.5 0
   r 1.5
}

object {
   shader fire0
   type sphere
   name sphere3
   c 3 1.5 0
   r 1.5
}

object {
   shader marble0
   type sphere
   name sphere4
   c -3 -1.5 0
   r 1.5
}

object {
   shader wood0
   type sphere
   name sphere5
   c 3 -1.5 0
   r 1.5
}

