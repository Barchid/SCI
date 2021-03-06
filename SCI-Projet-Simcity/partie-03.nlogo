; @author BARCHID Sami

extensions [
 table
]

; breeds
breed [cars car] ; voitures qui parcourent les routes
breed [houses house] ; maisons contenant des habitants
breed [water-towers water-tower] ; château d'eau fournissant de l'eau (water-supply)
breed [power-stations power-station] ; centrale électrique fournissant de l'elec (elec-supply)
breed [water-supplies water-supply] ; fourniture d'eau parcourant les routes pour alimenter les maisons dans le besoin
breed [elec-supplies elec-supply] ; fourniture d'elec parcourant les routes pour alimenter les maisons dans le besoin

; variables
houses-own [
  occupation ; nombre d'occupants de la maison
  elec ; capacité courante en electricité de la maison
  water ; capacité courante en eau de la maison
]

cars-own [
  local ; maison occupée par la voiture
]

;##################################################################################
;#################################### SETUP #######################################
to setup
  ca
  build-road

  ; Création des houses
  create-houses nb-houses [init-house]

  ; Création des centrales électriques
  create-power-stations nb-power-stations [init-power-station]

  ; Création des châteaux d'eau
  create-water-towers nb-water-towers [init-water-tower]

  ; Correction du biais de départ
  if(bias-correction?) [
    place-on-road
  ]

  reset-ticks
end

; fonction lancée pour tenter de corriger le biais de départ
; Donne une chance de placer des elec-supplies, water-supplies et des cars
to place-on-road
  repeat int(elec-max / 10 + water-max / 10) [
    place-cars-on-road
    place-power-stations-on-road
    place-water-towers-on-road
  ]
end

to place-cars-on-road
  let houss [self] of houses
  foreach houss [ hous ->
    let occ [occupation] of hous
    if occ > 0 and random car-frequence = 0 [
      ; placer voiture
      create-cars 1 [
        init-car hous
        move-to one-of patches with [pcolor = white]
        face one-of neighbors4 with [pcolor = white]
      ]
      ask hous [set occupation occupation - 1]
    ]
  ]
end

to place-power-stations-on-road
  let powers [self] of power-stations
  foreach powers [ power ->
    if random elec-frequence = 0 [
      create-elec-supplies 1 [
        init-elec-supply power
        move-to one-of patches with [pcolor = white]
        face one-of neighbors4 with [pcolor = white]
      ]
    ]
  ]
end

to place-water-towers-on-road
  let towers [self] of water-towers
  foreach towers [ tower ->
    if random water-frequence = 0 [
      create-water-supplies 1 [
        init-water-supply tower
        move-to one-of patches with [pcolor = white]
        face one-of neighbors4 with [pcolor = white]
      ]
    ]
  ]
end

; construire les patchs pour faire la route
to build-road
  ; Contours et croix au centre de la map = route
  ask patches with [
    abs(pxcor) = max-pxcor
    or
    abs(pycor) = max-pycor
    or
    pxcor = 0
    or
    pycor = 0
    or
    abs(pxcor) = (max-pxcor / 2)
    or
    abs(pycor) = (max-pycor / 2)
  ] [set pcolor white]

  ; ajouter des obstacles un peu moins probables
  ask patches with [pxcor = -13 and pycor < -11] [set pcolor white]
  ask patches with [pxcor > 8 and pycor = -16] [set pcolor black]
  ask patches with [pxcor >= -3 and pxcor <= 0 and pycor = 13] [set pcolor white]
  ask patches with [pxcor >= -15 and pxcor <= -9 and pycor = -5] [set pcolor white]
  ask patches with [pxcor >= 15 and pxcor <= -12 and pycor = 12] [set pcolor white]
  ask patches with [pxcor = 12 and pycor >= 1 and pycor <= 6] [set pcolor white]
  ask patches with [pxcor = -13 and pycor >= -4 and pycor <= -1] [set pcolor white]
  ask patches with [pxcor = -11 and pycor >= -7 and pycor <= -6] [set pcolor white]
  ask patches with [pxcor = -5 and pycor = 8] [set pcolor black]
  ask patches with [pxcor = 8 and pycor >= 3 and pycor <= 5] [set pcolor black]
  ask patches with [pxcor >= -2 and pxcor <= 2 and pycor >= -2 and pycor <= 2] [set pcolor black]
end

; go loop
to go
  if nb-ticks-max != 0 and nb-ticks-max - 1 < ticks [
   stop
  ]

  ask cars [car-decide]
  ask elec-supplies [elec-supply-decide]
  ask water-supplies [water-supply-decide]
  ask houses [house-decide]
  ask power-stations [power-station-decide]
  ask water-towers [water-tower-decide]

  tick
end



;########################## AVANCEMENT AU HASARD ##########################
;##########################################################################
; Fonction permettant à car, water-supply ou elec-supply d'avancer sur la route
to advance
  let f patch-ahead 1
  let r patch-right-and-ahead 90 1
  let l patch-left-and-ahead 90 1
  let dirs (patch-set f r l)  with [pcolor = white]

  ; Déplacement
  ; SI [je peux avancer quelque part]
  ifelse any? dirs
    [ ; Je choisis une direction au hasard
      move-to one-of dirs
    ]
    [ ; je rebrousse chemin (cul de sac oblige)
      move-to patch-left-and-ahead 180 1
      left 180
    ]

  if (patch-here = r) [right 90]
  if (patch-here = l) [left 90]
end



;##################################################################################
;#################################### CAR #########################################
; Initialisation d'une car
to init-car [new-local] ; new-local est la maison par laquel sort la voiture
  set shape "car top"

  ; Attribution de la maison de la voiture
  set local new-local

  ; sélectionner morceau de route où la voiture se place devant sa maison
  let road-available [neighbors4 with [pcolor = white]] of local
  move-to one-of road-available

  ; Se diriger là où on veut
  face one-of neighbors4 with [pcolor = white]

  ; uniquement pour l'affichage
  set label ""
  set size 2
end

to car-decide
  advance
  let near-houses houses-on neighbors4

  if any? near-houses with [self = [local] of myself] [
    car-interact-local
  ]
end

; Fonction appelée quand une voiture arrive devant sa maison (rentre chez lui en gros)
to car-interact-local
  ask local [set occupation (occupation + 1)]
  die
end



;##################################################################################
;#################################### HOUSE #######################################
; initialisation d'une house
to init-house
  set shape "house"
  set size 1

  ; Pour éviter qu'une maison soit grise (car le gris est la couleur d'une house morte)
  if color = grey [
    set color blue
  ]

  ; nombre d'habitants de la maison entre 1 et nbOccupations
  set occupation (random (nb-occupation) + 1)

  ; initialiser les capacités en eau et elec
  set elec elec-max
  set water water-max

  ; affichage
  if display-occupation [
    set label occupation
  ]

  ; bouger à un endroit juste à côté d'une route (personne d'autre dessus)
  move-to one-of patches with [pcolor = black and (any? neighbors4 with [pcolor = white]) and (count turtles-here = 0)]
end

to house-decide
  ; ne fait rien si la maison est morte (en gris)
  if color = grey [
    stop
  ]

  ; chier une car si je peux et si j'en ai le droit
  if occupation > 0 and (random car-frequence) = 0 [
    hatch-cars 1 [init-car myself]
    set occupation (occupation - 1)
  ]

  ; Consommation d'elec
  set elec (elec - occupation)

  ; Consommation d'eau
  set water (water - occupation)

  ; mort si eau ou elec <= 0
  if water <= 0 or elec <= 0 [
    house-ko
  ]

  ; mettre à jour le nombre de cars affiché (si demandé)
  if display-occupation [
    set label occupation
  ]
end

; fonction de mort d'une maison (pour cause de pénurie d'elec/eau)
to house-ko
  set color grey
  set occupation 0 ; les habitants meurent tous
  set water 0 ; plus d'eau car la maison est "détruite"
  set elec 0 ; plus d'elec car la maison est "détruite"
  ask cars with [local = myself] [die] ; mort directe des cars
end



;##################################################################################
;#################################### POWER STATION ###############################
; Initialisation d'une centrale électrique
to init-power-station
  set shape "power-station"
  set size 1
  ; bouger à un endroit juste à côté d'une route (sans aucune autre installation dessus)
  move-to one-of patches with [pcolor = black and (any? neighbors4 with [pcolor = white]) and (count turtles-here = 0)]
end

; Fonction de décision d'une centrale électrique
to power-station-decide
  ; chier un elec-supply suivant la fréquence entrée dans le slider
  if (random elec-frequence) = 0 [
    hatch-elec-supplies 1 [init-elec-supply myself]
  ]
end



;##################################################################################
;#################################### WATER TOWER ###############################
; Initialisation d'un chateau d'eau
to init-water-tower
  set shape "water-tower"
  set size 1
  ; bouger à un endroit juste à côté d'une route (sans aucune autre installation dessus)
  move-to one-of patches with [pcolor = black and (any? neighbors4 with [pcolor = white]) and (count turtles-here = 0)]
end

; Fonction de décision d'un chateau d'eau
to water-tower-decide
  ; chier un water-supply suivant la fréquence entrée dans le slider
  if (random water-frequence) = 0 [
    hatch-water-supplies 1 [init-water-supply myself]
  ]
end



;##################################################################################
;#################################### ELEC SUPPLY #################################
; Initialisation d'un elec supply
to init-elec-supply [station] ; station est la centrale qui a créé le elec-supply
  set shape "elec"
  set size 1

  ; sélectionner morceau de route où le elec-supply se place devant sa centrale
  let road-available [neighbors4 with [pcolor = white]] of station
  move-to one-of road-available

  ; Se diriger là où on veut
  face one-of neighbors4 with [pcolor = white]
end

; Fonction de décision d'un elec-supply
to elec-supply-decide
  ; avancer
  advance

  ; Recharger une maison proche (pas morte) dans le besoin (si elle existe)
  let near-houses houses-on neighbors4
  if any? near-houses with [elec < 0.7 * elec-max and color != grey] [
    ask one-of near-houses with [elec < 0.7 * elec-max] [set elec elec-max]
    die ; meurt après le premier rechargement
  ]
end



;##################################################################################
;#################################### WATER SUPPLY ################################
; Initialisation d'un water supply
to init-water-supply [tower] ; tower est le chateau d'eau qui a créé le water-supply
  set shape "water"
  set size 1

  ; sélectionner morceau de route où le water-supply se place devant son château d'eau
  let road-available [neighbors4 with [pcolor = white]] of tower
  move-to one-of road-available

  ; Se diriger là où on veut
  face one-of neighbors4 with [pcolor = white]
end

; Fonction de décision d'un water-supply
to water-supply-decide
  ; avancer
  advance

  ; Recharger une maison proche (pas morte) dans le besoin (si elle existe)
  let near-houses houses-on neighbors4
  if any? near-houses with [water < 0.7 * water-max and color != grey] [
    ask one-of near-houses with [water < 0.7 * water-max] [set water water-max]
    die ; meurt après le premier rechargement
  ]
end


; Fonction de mise à jour de l'histogramme des états des maisons
to update-houses-hist
  set-current-plot "État des maisons"
  clear-plot

  let counts table:make
  table:put counts "Double manque" count houses with [elec < (elec-max / 10) and water < (water-max / 10)]
  table:put counts "Manque électricité" count houses with [elec < (elec-max / 10) ]
  table:put counts "Manque eau" count houses with [water < (water-max / 10)]
  table:put counts "Tout va bien" count houses with [elec >= (elec-max / 10) and water >= (water-max / 10)]

  let directions sort table:keys counts
  let n length directions
  set-plot-x-range 0 n
  let step 0.05 ; tweak this to leave no gaps
  (foreach directions range n [ [d i] ->
    let y table:get counts d
    let c hsb (i * 360 / n) 50 75
    create-temporary-plot-pen d
    set-plot-pen-mode 1 ; bar mode
    set-plot-pen-color c
    foreach (range 0 y step) [ _y -> plotxy i _y ]
    set-plot-pen-color black
    plotxy i y
    set-plot-pen-color c ; to get the right color in the legend
  ])
end
@#$#@#$#@
GRAPHICS-WINDOW
998
19
1747
769
-1
-1
22.455
1
10
1
1
1
0
0
0
1
-16
16
-16
16
0
0
1
ticks
30.0

BUTTON
266
44
598
77
Setup
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
614
45
928
78
Go
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

SWITCH
12
43
246
76
display-occupation
display-occupation
0
1
-1000

SLIDER
529
134
749
167
nb-occupation
nb-occupation
0
100
50.0
1
1
NIL
HORIZONTAL

SLIDER
525
228
746
261
nb-houses
nb-houses
1
100
100.0
1
1
NIL
HORIZONTAL

SLIDER
797
138
969
171
car-frequence
car-frequence
0
200
15.0
1
1
NIL
HORIZONTAL

SLIDER
247
137
478
170
water-max
water-max
10
5000
5000.0
10
1
NIL
HORIZONTAL

SLIDER
247
229
481
262
elec-max
elec-max
10
5000
5000.0
10
1
NIL
HORIZONTAL

SLIDER
247
314
482
347
water-frequence
water-frequence
0
200
200.0
1
1
NIL
HORIZONTAL

SLIDER
237
399
486
432
elec-frequence
elec-frequence
0
200
200.0
1
1
NIL
HORIZONTAL

SLIDER
525
313
747
346
nb-power-stations
nb-power-stations
1
100
2.0
1
1
NIL
HORIZONTAL

SLIDER
525
399
747
432
nb-water-towers
nb-water-towers
1
100
2.0
1
1
NIL
HORIZONTAL

PLOT
12
455
681
798
Population des maisons en fonction du temps
Temps (ticks)
Nombre de maisons
0.0
10.0
0.0
10.0
true
true
"" ""
PENS
"Total" 1.0 0 -16777216 true "" "plot count houses"
"Mortes" 1.0 0 -2674135 true "" "plot count houses with [color = grey]"
"Vivantes" 1.0 0 -10899396 true "" "plot count houses with [color != grey]"

MONITOR
702
455
810
500
Maisons vivantes
count houses with [color != grey]
17
1
11

MONITOR
704
522
893
567
Maisons mortes
count houses with [color = grey]
17
1
11

PLOT
9
1095
680
1466
Quantité de ressources en fonction du temps
Temps (en ticks)
Nombre de ressources
0.0
10.0
0.0
10.0
true
true
"" ""
PENS
"Électricité" 1.0 0 -1184463 true "" "plot count elec-supplies"
"Eau" 1.0 0 -13345367 true "" "plot count water-supplies"

MONITOR
711
1098
929
1143
Nombre d'électricités en déplacement
count elec-supplies
17
1
11

MONITOR
711
1167
930
1212
Nombre d'eau en déplacement
count water-supplies
17
1
11

SLIDER
11
139
183
172
nb-ticks-max
nb-ticks-max
0
100000
25000.0
100
1
NIL
HORIZONTAL

PLOT
963
799
1719
1080
Démographie
Temps (ticks)
Nombre d'habitants
0.0
10.0
0.0
10.0
true
true
"" ""
PENS
"À la maison" 1.0 0 -5825686 true "" "plot sum [occupation] of houses"
"Sur la route" 1.0 0 -11221820 true "" "plot count cars"
"Total" 1.0 0 -955883 true "" "plot count cars + sum [occupation] of houses"

MONITOR
1729
802
1835
847
Population totale
sum [occupation] of houses + count cars
17
1
11

MONITOR
1728
874
1890
919
Population en déplacement
count cars
17
1
11

MONITOR
1727
941
1860
986
Population sédentaire
sum [occupation] of houses
17
1
11

PLOT
9
824
682
1059
État des maisons
Temps (ticks)
Nombre de maisons
0.0
10.0
0.0
10.0
true
true
"" ""
PENS
"Double manque" 1.0 0 -2674135 true "" "plot count houses with [elec < (elec-max / 10) and water < (water-max / 10)]"
"Manque électricité" 1.0 0 -1184463 true "" "plot count houses with [elec < (elec-max / 10)]"
"Manque eau" 1.0 0 -13791810 true "" "plot count houses with [water < (water-max / 10)]"
"Tout va bien" 1.0 0 -13840069 true "" "plot count houses with [elec >= (elec-max / 10) and water >= (water-max / 10)]"

MONITOR
697
828
858
873
Maisons manque électricité
count houses with [elec < (elec-max / 10)]
17
1
11

MONITOR
699
886
861
931
Maisons manque eau
count houses with [water < (water-max / 10)]
17
1
11

MONITOR
699
952
887
997
Maisons manque ressources
count houses with [elec < (elec-max / 10) or water < (water-max / 10)]
17
1
11

MONITOR
700
1011
868
1056
Maisons tout va bien
count houses with [elec > (elec-max / 10) and water > (water-max / 10)]
17
1
11

SWITCH
13
229
185
262
bias-correction?
bias-correction?
0
1
-1000

TEXTBOX
13
11
163
39
Afficher l'occupation des maisons ?
11
0.0
1

TEXTBOX
15
108
165
136
Durée de la simulation (0 pour une simulation infinie)
11
0.0
1

TEXTBOX
16
201
166
229
Activer la correction du biais de départ
11
0.0
1

TEXTBOX
248
199
472
227
Stock d'éléctricité pour une maison
11
0.0
1

TEXTBOX
250
118
400
136
Stock d'eau pour une maison
11
0.0
1

TEXTBOX
249
286
399
304
Fréquence de production d'eau
11
0.0
1

TEXTBOX
241
369
464
397
Fréquence de production d'électricité
11
0.0
1

TEXTBOX
797
107
947
135
Fréquence de sortie des voitures
11
0.0
1

TEXTBOX
531
111
765
139
Nombre maximal d'habitants dans une maison
11
0.0
1

TEXTBOX
528
205
678
223
Nombre de maisons
11
0.0
1

TEXTBOX
528
292
710
320
Nombre de centrales électriques
11
0.0
1

TEXTBOX
527
379
677
397
Nombre de châteaux d'eau
11
0.0
1

@#$#@#$#@
## WHAT IS IT?

(a general understanding of what the model is trying to show or explain)

## HOW IT WORKS

(what rules the agents use to create the overall behavior of the model)

## HOW TO USE IT

(how to use the model, including a description of each of the items in the Interface tab)

## THINGS TO NOTICE

(suggested things for the user to notice while running the model)

## THINGS TO TRY

(suggested things for the user to try to do (move sliders, switches, etc.) with the model)

## EXTENDING THE MODEL

(suggested things to add or change in the Code tab to make the model more complicated, detailed, accurate, etc.)

## NETLOGO FEATURES

(interesting or unusual features of NetLogo that the model uses, particularly in the Code tab; or where workarounds were needed for missing features)

## RELATED MODELS

(models in the NetLogo Models Library and elsewhere which are of related interest)

## CREDITS AND REFERENCES

(a reference to the model's URL on the web if it has one, as well as any other necessary credits, citations, and links)
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

car top
true
0
Polygon -7500403 true true 151 8 119 10 98 25 86 48 82 225 90 270 105 289 150 294 195 291 210 270 219 225 214 47 201 24 181 11
Polygon -16777216 true false 210 195 195 210 195 135 210 105
Polygon -16777216 true false 105 255 120 270 180 270 195 255 195 225 105 225
Polygon -16777216 true false 90 195 105 210 105 135 90 105
Polygon -1 true false 205 29 180 30 181 11
Line -7500403 false 210 165 195 165
Line -7500403 false 90 165 105 165
Polygon -16777216 true false 121 135 180 134 204 97 182 89 153 85 120 89 98 97
Line -16777216 false 210 90 195 30
Line -16777216 false 90 90 105 30
Polygon -1 true false 95 29 120 30 119 11

car-rotatable
true
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

car-rotate
true
13
Rectangle -2064490 true true 90 15 210 300
Polygon -16777216 true false 105 90 195 90 180 150 120 150 105 90
Rectangle -16777216 true false 105 240 195 255
Rectangle -2674135 true false 90 285 105 300
Rectangle -2674135 true false 195 285 210 300
Rectangle -1184463 true false 195 15 210 30
Rectangle -1184463 true false 90 15 105 30
Rectangle -16777216 true false 195 120 210 210
Rectangle -16777216 true false 90 120 105 210

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

elec
true
0
Circle -16777216 true false 0 0 300
Rectangle -1184463 true false 60 135 255 165
Polygon -1184463 true false 30 165 90 0 195 0 90 165
Polygon -1184463 true false 285 135 135 300 135 300 240 135

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

power-station
false
0
Polygon -7500403 true true 45 255 60 60 90 60 105 255
Rectangle -7500403 true true 105 165 255 255
Polygon -16777216 true false 120 165 120 195 180 165 105 165
Polygon -16777216 true false 180 165 180 195 255 165 180 165
Rectangle -16777216 true false 120 210 165 225
Rectangle -16777216 true false 195 210 240 225
Polygon -6459832 true false 75 45 90 30 120 30 135 15 150 15 135 30 120 45 105 45 90 60 75 45

sheep
false
15
Circle -1 true true 203 65 88
Circle -1 true true 70 65 162
Circle -1 true true 150 105 120
Polygon -7500403 true false 218 120 240 165 255 165 278 120
Circle -7500403 true false 214 72 67
Rectangle -1 true true 164 223 179 298
Polygon -1 true true 45 285 30 285 30 240 15 195 45 210
Circle -1 true true 3 83 150
Rectangle -1 true true 65 221 80 296
Polygon -1 true true 195 285 210 285 210 240 240 210 195 210
Polygon -7500403 true false 276 85 285 105 302 99 294 83
Polygon -7500403 true false 219 85 210 105 193 99 201 83

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

water
true
0
Polygon -13791810 true false 150 300 75 135 225 135
Circle -13791810 true false 60 15 180

water-tower
false
0
Polygon -7500403 true true 90 75 150 0 210 75
Rectangle -7500403 true true 75 165 225 180
Rectangle -7500403 true true 90 90 210 150
Polygon -7500403 true true 195 180 180 180 225 270 240 270 210 210
Polygon -7500403 true true 105 180 120 180 75 270 60 270 90 210
Polygon -7500403 true true 165 165 195 165 120 270 90 270 90 270
Polygon -7500403 true true 135 165 105 165 180 270 210 270 210 270

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

wolf
false
0
Polygon -16777216 true false 253 133 245 131 245 133
Polygon -7500403 true true 2 194 13 197 30 191 38 193 38 205 20 226 20 257 27 265 38 266 40 260 31 253 31 230 60 206 68 198 75 209 66 228 65 243 82 261 84 268 100 267 103 261 77 239 79 231 100 207 98 196 119 201 143 202 160 195 166 210 172 213 173 238 167 251 160 248 154 265 169 264 178 247 186 240 198 260 200 271 217 271 219 262 207 258 195 230 192 198 210 184 227 164 242 144 259 145 284 151 277 141 293 140 299 134 297 127 273 119 270 105
Polygon -7500403 true true -1 195 14 180 36 166 40 153 53 140 82 131 134 133 159 126 188 115 227 108 236 102 238 98 268 86 269 92 281 87 269 103 269 113

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270
@#$#@#$#@
NetLogo 6.1.1
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180
@#$#@#$#@
0
@#$#@#$#@
