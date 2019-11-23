; @author BARCHID Sami

extensions [
  array ; utile pour le BFS-advance
]

__includes [
  "queue.nls" ; utile pour le BFS-advance
]

patches-own [
  bfs-distance ; distance utilisée pour le BFS-advance (recherche du meilleur chemin)
]

; breeds
breed [inhabitants inhabitant] ; habitants de la ville qui habitent dans les maisons, vont travailler aux centrales/châteaux d'eau et prennent la voiture
breed [job-offers job-offer] ; offre d'emploi parcourant la route pour trouver un inhabitant sans travail
breed [houses house] ; maisons contenant des habitants et consommant de l'elec/water
breed [water-towers water-tower] ; château d'eau fournissant de l'eau (water-supply)
breed [power-stations power-station] ; centrale électrique fournissant de l'elec (elec-supply)
breed [water-supplies water-supply] ; fourniture d'eau parcourant les routes pour alimenter les maisons dans le besoin
breed [elec-supplies elec-supply] ; fourniture d'elec parcourant les routes pour alimenter les maisons dans le besoin
breed [clocks clock] ; horloge qui affiche l'heure de la journée (complètement inutile mais c'est marrant)

; globals
globals [
  ticks-per-day ; nombre de ticks par jour
]

; variables de chaque turtle
houses-own [
  elec ; capacité courante en electricité de la maison
  water ; capacité courante en eau de la maison
]

inhabitants-own [
  local ; maison habitée
  employer ; employeur du mec
  destination ; patch vers lequel l'habitant doit se diriger
  building-destination ; bâtiment vers lequel l'habitant doit aller (lié à destination), peut être une house, un power-station ou une water-tower
  working-hour ; numéro du tier de la journée pour bosser (1 = 00:00-07:59, 2 = 08:00-15:59, 3 = 16:00 - 23;59)
]

job-offers-own [
  boss ; patron qui a lancé l'offre d'emploi
  working-hour ; numéro du tier de la journée pour bosser (1 = 00:00-07:59, 2 = 08:00-15:59, 3 = 16:00 - 23;59)
]

;##################################################################################
;#################################### SETUP #######################################
to setup
  ca
  build-road

  ; nombre de ticks par jour
  set ticks-per-day 24 * ticks-per-hour

  ; Création des houses
  create-houses nb-houses [init-house]

  ; Création des centrales électriques
  create-power-stations nb-power-stations [init-power-station]

  ; Création des châteaux d'eau
  create-water-towers nb-water-towers [init-water-tower]

  ; créer l'horloge qui affichera l'heure
  create-clocks 1 [init-clock]

  reset-ticks
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
  ask clocks [clock-decide]
  ask inhabitants [inhabitant-decide]
  ask elec-supplies [elec-supply-decide]
  ask water-supplies [water-supply-decide]
  ask houses [house-decide]
  ask power-stations [power-station-decide]
  ask water-towers [water-tower-decide]
  ask job-offers [job-offer-decide]
  tick
end



;########################## FONCTIONS UTILITAIRES #########################
;##########################################################################
; Fonction permettant à inhabitant, water-supply ou elec-supply d'avancer sur la route
to rand-advance
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

; Fonction d'avancement pour arriver le plus rapidement possible vers la destination en paramètre (utilisant le breadth first search)
; https://www.redblobgames.com/pathfinding/tower-defense/ pour comprendre la théorie
to bfs-advance [dest]
  ; Initialisation de la file
  queue-init (count patches with [pcolor = white]) ; initialiser la file (maximum possible d'éléments = les patches de route (en blanc)
  queue-push dest

  ; Initialiser la distance à la destination à -1 pour tout le monde (-1 veut dire qu'on n'a pas encore évalué la distance)
  ask patches with [pcolor = white] [set bfs-distance nobody]
  ask dest [set bfs-distance 0]


  while [not queue-empty?] [
    let current queue-poll
    let distance-current ([bfs-distance] of current)

    let near-roads [neighbors4 with [pcolor = white and bfs-distance = nobody]] of current
    ask near-roads [
      queue-push self
      set bfs-distance (1 + distance-current)
    ]
  ]

  ; CHOISIR le meilleur voisin du patch courant (poru le plus court chemin)
  ; se déplacer vers le morceau de route qui montre le plus rapide chemin
  face min-one-of neighbors4 with [pcolor = white] [bfs-distance]
  move-to min-one-of neighbors4 with [pcolor = white] [bfs-distance]
end

; retourne un identifiant pour le patch en paramètre (utilisé pour la gestion de la hashmap dans le bfs)
to-report id-of-patch [ptch]
  report list ([pxcor] of ptch) ([pycor] of ptch)
end

; numéro de l'heure de la journée
to-report get-hour
  let ticks-of-day (ticks mod ticks-per-day) ; nombre de ticks écoulés dans la journée
  report int (ticks-of-day / ticks-per-hour)
end

; Fonction pour récupérer le numéro du tier de la journée
to-report get-day-tier
  let hour get-hour ; récupérer l'heure

  if hour < 8 [
    report 1
  ]

  if hour < 16 [
    report 2
  ]

  report 3
end



;##################################################################################
;#################################### INHABITANT ##################################
; Initialisation d'une inhabitant
to init-inhabitant [new-local] ; new-local est la house où le inhabitant habite
  set shape "car top"
  set hidden? true

  ; Attribution de la maison du inhabitant
  set local new-local
  move-to local


  ; Un inhabitant créé n'a pas de destination
  set destination nobody
  set building-destination nobody

  ; Un inhabitant créé n'a pas d'employeur
  set employer nobody

  ; uniquement pour l'affichage
  set size 2
  set color [color] of local ; même couleur que la maison pour faire un poil de rapprochement
end

to inhabitant-decide
  ; SI [je suis sur la route vers une destination] -> continuer d'y aller (SI je suis à côté de la destination, je rentre)
  if ([pcolor] of patch-here = white) [
    inhabitant-move
    stop
  ]

  ; SI [je suis un carolo (je n'ai pas de boulot et je suis donc chez moi)] -> go se toucher full
  if employer = nobody [
    ; je me touche de ouf
    stop
  ]

  ; SI [je suis chez moi et je n'ai pas de travail à ce tier de la journée] -> je me touche chez oim
  if patch-here = [patch-here] of local and working-hour != get-day-tier [
    ; je me touche-zer
    stop
  ]

  ; SI [je suis chez moi et j'ai un travail à ce tier de la journée] -> je sors de chez oim et mon boulot devient ma destination
  if patch-here = [patch-here] of local and working-hour = get-day-tier [
    inhabitant-go-to-work
  ]

  ; SI [je suis à l'usine et c'est toujours mon horaire de boulot] -> je bosse et je ne fais rien d'autre
  if patch-here = [patch-here] of employer and working-hour = get-day-tier [
    ; je me touche-zer
    stop
  ]

  ; SI [je suis à l'usine et mon horaire de boulot est fini] -> je me casse de l'usine, ma maison devient ma destination
  if patch-here = [patch-here] of employer and working-hour != get-day-tier [
    inhabitant-go-home
  ]
end

; Fonction appelée quand le inhabitant doit sortir du bâtiment où il se trouve
to inhabitant-leave [building] ; building est le bâtiment duquel le inhabitant doit sortir
  set hidden? false ; la voiture du inhabitant devient visible

  ; sélectionner morceau de route où la voiture se place devant sa maison
  let road-available [neighbors4 with [pcolor = white]] of building
  move-to one-of road-available

  ; Faire face à la route (uniquement de l'affichage)
  face one-of neighbors4 with [pcolor = white]
end

; Fonction qui permet au inhabitant d'entrer dans un bâtiment
to inhabitant-enter [building] ; building est le bâtiment dans lequel le inhabitant doit rentrer
  set hidden? true ; devient invisible
  move-to building ; se transporter sur la case du batiment
  set destination nobody ; je suis arrivé à bon port, j'arrête
  set building-destination nobody
end

; se déplace vers sa destination (ou rentre dans le bâtiment si je suis arrivé devant)
to inhabitant-move
  ; SI [je suis devant mon bâtiment (et donc arrivé à destination)]
  ifelse patch-here = destination [
    inhabitant-enter building-destination
  ]
  ; SINON, j'avance au plus court chemin
  [
    bfs-advance destination
  ]
end

; Sort de chez lui et va à son lieu de travail
to inhabitant-go-to-work
  inhabitant-leave local
  set destination one-of [neighbors4 with [pcolor = white]] of employer
  set building-destination employer
end

; Sort de son lieu de travail et va chez lui
to inhabitant-go-home
  inhabitant-leave employer
  set destination one-of [neighbors4 with [pcolor = white]] of local
  set building-destination local
end

to test-dist


  ask patches with [pxcor = -13 and pycor < -11] [set pcolor white]
  ;ask patches with [pycor =
end

to test-2
  ask patches [set plabel pxcor]
end

to test-color

end

; Fonction appelée quand le inhabitant se fait virer de son travail
to inhabitant-getting-fired
  ; le code qui suit est la routine quotidienne d'un habitant de Charleroi
  inhabitant-leave employer ; je quitte l'usine
  set destination one-of [neighbors4 with [pcolor = white]] of local ; je m'apprête à rentrer chez moi
  set building-destination local
  set employer nobody ; je suis au chômage
end

; Fonction appelée quand le inhabitant se fait engager par une job-offer
to inhabitant-getting-hired [offer]
  set employer [boss] of offer
  set working-hour [working-hour] of offer
end


;##################################################################################
;#################################### HOUSE #######################################
; initialisation d'une house
to init-house
  set shape "house"
  set size 1

  ; Pour éviter qu'une maison soit grise (vu que le gris est la couleur d'une house morte)
  if color = grey [
    set color blue
  ]

  ; bouger à un endroit juste à côté d'une route (avec personne d'autre dessus)
  move-to one-of patches with [pcolor = black and (any? neighbors4 with [pcolor = white]) and (count turtles-here = 0)]

  ; créer les inhabitants de la house
  let occupation (random (nb-occupations) + 1) ; nombre d'habitants de la maison entre 1 et nb-occupations
  hatch-inhabitants occupation [init-inhabitant myself]

  ; initialiser les capacités en eau et elec
  set elec elec-max
  set water water-max

  ; affichage
  if display-occupation [
    set label occupation
  ]
end

to house-decide
  ; ne fait rien si la maison est morte (en gris)
  if color = grey [
    stop
  ]


  let occupation count inhabitants with [(pycor = [pycor] of myself) and (pxcor = [pxcor] of myself)]

  ; Consommation d'elec
  set elec (elec - occupation)

  ; Consommation d'eau
  set water (water - occupation)

  ; mort si eau ou elec <= 0
  if water <= 0 or elec <= 0 [
    house-ko
  ]

  ; mettre à jour le nombre de inhabitants affiché (si demandé)
  if display-occupation [
    set label occupation
    if color = grey [
      set label 0
    ]
  ]
end

; fonction de mort d'une maison (pour cause de pénurie d'elec/eau)
to house-ko
  set color grey
  set water 0 ; plus d'eau inhabitant la maison est "détruite"
  set elec 0 ; plus d'elec inhabitant la maison est "détruite"
  ask inhabitants with [local = myself] [die] ; mort directe des inhabitants
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
  power-station-supply

  ; Chance de virer un gitan toutes les heures
  power-station-firing

  ; Engager des gitans si on est en manque d'effectif
  power-station-hiring
end

; Fonction qui crée du elec-supply
to power-station-supply
  ; chier un elec-supply suivant la fréquence entrée dans le slider et les travailleurs
  let nb-workers count inhabitants-on patch-here ; nombre de travailleurs sur place (donc qui travaillent)
  if (random elec-frequence) < nb-workers  [
    hatch-elec-supplies 1 [init-elec-supply myself] ; plus il y a de travailleurs,  plus on a de chance de produire de l'elec
  ]
end

; Fonction pour virer un employé (à chaque début d'une nouvelle heure, avec une chance qu'il reste)
to power-station-firing
  let new-hour? (ticks mod ticks-per-hour) = 0 ; flag indiquant si on est dans une nouvelle heure

  ; Tirage au sort pour voir si je vire quelqu'un
  if new-hour? and (random power-station-firing-frequence) = 0 and any? inhabitants-on patch-here [
    ask one-of inhabitants-on patch-here [inhabitant-getting-fired] ; virer le peye
  ]
end

; FOnction pour engager un gitan si on manque d'effectif
to power-station-hiring
  ; POUR CHAQUE tier de la journée, on vérifie qu'on a/cherche déjà assez de gitans, sinon on lance des job-offers

  ; de 00:00 à 07:59
  let nb-workers (count inhabitants with [employer = myself and working-hour = 1 ]) + (count job-offers with [boss = myself and working-hour = 1])
  if nb-workers < power-station-max-workers [
    hatch-job-offers (power-station-max-workers - nb-workers) [init-job-offer myself 1]
  ]

  ; de 08:00 à 15:59
  set nb-workers (count inhabitants with [employer = myself and working-hour = 2 ]) + (count job-offers with [boss = myself and working-hour = 2])
  if nb-workers < power-station-max-workers [
    hatch-job-offers (power-station-max-workers - nb-workers) [init-job-offer myself 2]
  ]

  ; de 16:00 à 23:59
  set nb-workers (count inhabitants with [employer = myself and working-hour = 3 ]) + (count job-offers with [boss = myself and working-hour = 3])
  if nb-workers < power-station-max-workers [
    hatch-job-offers (power-station-max-workers - nb-workers) [init-job-offer myself 3]
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
  water-tower-supply

  ; Chance de virer un gitan toutes les heures
  water-tower-firing

  ; Engager des gitans si on est en manque d'effectif
  water-tower-hiring
end

; Fonction qui crée du water-supply
to water-tower-supply
  ; chier un water-supply suivant la fréquence entrée dans le slider et les travailleurs
  let nb-workers count inhabitants-on patch-here ; nombre de travailleurs sur place (donc qui travaillent)
  if (random water-frequence) < nb-workers  [
    hatch-water-supplies 1 [init-water-supply myself] ; plus il y a de travailleurs,  plus on a de chance de produire de la water
  ]
end

; Fonction pour virer un employé (à chaque début d'une nouvelle heure, avec une chance qu'il reste)
to water-tower-firing
  let new-hour? (ticks mod ticks-per-hour) = 0 ; flag indiquant si on est dans une nouvelle heure

  ; Tirage au sort pour voir si je vire quelqu'un
  if new-hour? and (random water-tower-firing-frequence) = 0 and any? inhabitants-on patch-here [
    ask one-of inhabitants-on patch-here [inhabitant-getting-fired] ; virer le peye
  ]
end

; FOnction pour engager un gitan si on manque d'effectif
to water-tower-hiring
  ; POUR CHAQUE tier de la journée, on vérifie qu'on a/cherche déjà assez de gitans, sinon on lance des job-offers

  ; de 00:00 à 07:59
  let nb-workers (count inhabitants with [employer = myself and working-hour = 1 ]) + (count job-offers with [boss = myself and working-hour = 1])
  if nb-workers < water-tower-max-workers [
    hatch-job-offers (water-tower-max-workers - nb-workers) [init-job-offer myself 1]
  ]

  ; de 08:00 à 15:59
  set nb-workers (count inhabitants with [employer = myself and working-hour = 2 ]) + (count job-offers with [boss = myself and working-hour = 2])
  if nb-workers < water-tower-max-workers [
    hatch-job-offers (water-tower-max-workers - nb-workers) [init-job-offer myself 2]
  ]

  ; de 16:00 à 23:59
  set nb-workers (count inhabitants with [employer = myself and working-hour = 3 ]) + (count job-offers with [boss = myself and working-hour = 3])
  if nb-workers < water-tower-max-workers [
    hatch-job-offers (water-tower-max-workers - nb-workers) [init-job-offer myself 3]
  ]
end



;##################################################################################
;#################################### ELEC SUPPLY #################################
; Initialisation d'un elec supply
to init-elec-supply [station] ; station est la centrale qui a créé le elec-supply
  set shape "elec"
  set size 1
  set hidden? not display-elec-supplies

  ; sélectionner morceau de route où le elec-supply se place devant sa centrale
  let road-available [neighbors4 with [pcolor = white]] of station
  move-to one-of road-available

  ; Se diriger là où on veut
  face one-of neighbors4 with [pcolor = white]
end

; Fonction de décision d'un elec-supply
to elec-supply-decide
  ; cacher s'il faut
  set hidden? not display-elec-supplies

  ; avancer
  rand-advance

  ; Recharger une maison proche (pas morte) dans le besoin (si elle existe)
  let near-houses houses-on neighbors4
  if any? near-houses with [elec < elec-max and color != grey] [
    ask one-of near-houses with [elec < elec-max] [set elec elec-max]
    die ; meurt après le premier rechargement
  ]
end



;##################################################################################
;#################################### WATER SUPPLY ################################
; Initialisation d'un water supply
to init-water-supply [tower] ; tower est le chateau d'eau qui a créé le water-supply
  set shape "water"
  set size 1
  set hidden? not display-water-supplies

  ; sélectionner morceau de route où le water-supply se place devant son château d'eau
  let road-available [neighbors4 with [pcolor = white]] of tower
  move-to one-of road-available

  ; Se diriger là où on veut
  face one-of neighbors4 with [pcolor = white]
end

; Fonction de décision d'un water-supply
to water-supply-decide
  ; cacher s'il faut
  set hidden? not display-water-supplies

  ; avancer
  rand-advance

  ; Recharger une maison proche (pas morte) dans le besoin (si elle existe)
  let near-houses houses-on neighbors4
  if any? near-houses with [water < water-max and color != grey] [
    ask one-of near-houses with [water < water-max] [set water water-max]
    die ; meurt après le premier rechargement
  ]
end



;##################################################################################
;#################################### JOB-OFFER ###################################
; Initialiser la job-offer
to init-job-offer [related-boss schedule] ; related-boss est le boss qui a lancé la job-offer
  set boss related-boss
  set working-hour schedule
  set shape "briefcase"
  set size 1
  set hidden? not display-job-offers

  ; sélectionner morceau de route où la job-offer se place devant l'employeur
  let road-available [neighbors4 with [pcolor = white]] of boss
  move-to one-of road-available

  ; Se diriger là où on veut sur la route
  face one-of neighbors4 with [pcolor = white]
end

to job-offer-decide
  ; cacher s'il faut
  set hidden? not display-job-offers

  rand-advance

  ; Vérifier si on ne croise pas un inhabitant au chômage dans sa maison (s'il est hidden? c'est qu'il est dans sa maison)
  let near-carolos inhabitants-on neighbors4
  if any? near-carolos with [employer = nobody and hidden?] [
    job-offer-recrute one-of near-carolos with [employer = nobody and hidden?]
  ]
end

; Donne la job-offer à un inhabitant
to job-offer-recrute [worker] ; worker est le inhabitant qui va choper l'emploi
  ask worker [inhabitant-getting-hired myself]
  die ; meurt quand la job-offer est acceptée
end



;##################################################################################
;#################################### CLOCK #######################################
; initialiser la clock
to init-clock
  move-to patch 4 4
  set size 3
  set shape "clock"
  set color white
  facexy 4 5; faire pointer l'horloge vers la dernière heure comme ça au premier appel du "go" on pointe sur 0
  lt 30
  set label 0
end

; Tourne quand l'heure avance pour symbolier le changemet d'heure
to clock-decide
  let new-hour? (ticks mod ticks-per-hour) = 0 ; flag indiquant si on est dans une nouvelle heure

  if new-hour? [
    set label get-hour
    rt 30 ; 30 degré = 360° / les 12 heures affichés par une horloge

    ; afficher le tier de la journée en haut de l'horloge
    ask patch 4 6 [
      set plabel get-day-tier
    ]
  ]
end

;##################################################################################
;################################## BOUTONS #######################################
; Fonction pour afficher/cacher les elec-supplies
to toggle-elec-supplies
  ask elec-supplies [set hidden? not hidden?]
end

; Fonction pour afficher/cacher les water-supplies
to toggle-water-supplies
  ask water-supplies [set hidden? not hidden?]
end

; Fonction pour afficher/cacher les job-offers
to toggle-job-offers
  ask job-offers [set hidden? not hidden?]
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
11
44
245
77
display-occupation
display-occupation
0
1
-1000

SLIDER
767
141
939
174
nb-houses
nb-houses
1
100
10.0
1
1
NIL
HORIZONTAL

SLIDER
251
109
482
142
water-max
water-max
1000
5000
5000.0
100
1
NIL
HORIZONTAL

SLIDER
250
157
484
190
elec-max
elec-max
1000
5000
5000.0
100
1
NIL
HORIZONTAL

SLIDER
765
192
937
225
nb-power-stations
nb-power-stations
1
100
4.0
1
1
NIL
HORIZONTAL

SLIDER
770
234
942
267
nb-water-towers
nb-water-towers
1
100
4.0
1
1
NIL
HORIZONTAL

SLIDER
188
355
383
388
water-tower-max-workers
water-tower-max-workers
1
100
5.0
1
1
NIL
HORIZONTAL

SLIDER
189
313
392
346
power-station-max-workers
power-station-max-workers
1
100
5.0
1
1
NIL
HORIZONTAL

SLIDER
770
286
942
319
nb-occupations
nb-occupations
1
100
21.0
1
1
NIL
HORIZONTAL

SLIDER
617
472
789
505
water-frequence
water-frequence
1
1000
51.0
10
1
NIL
HORIZONTAL

SLIDER
628
549
800
582
elec-frequence
elec-frequence
1
1000
41.0
10
1
NIL
HORIZONTAL

SLIDER
11
192
183
225
ticks-per-hour
ticks-per-hour
100
10000
100.0
100
1
NIL
HORIZONTAL

SLIDER
186
410
398
443
water-tower-firing-frequence
water-tower-firing-frequence
0
1000
20.0
10
1
NIL
HORIZONTAL

SLIDER
188
533
408
566
power-station-firing-frequence
power-station-firing-frequence
0
1000
20.0
10
1
NIL
HORIZONTAL

BUTTON
432
212
510
245
NIL
test-dist
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

SWITCH
26
624
180
657
display-job-offers
display-job-offers
1
1
-1000

SWITCH
237
670
406
703
display-elec-supplies
display-elec-supplies
1
1
-1000

SWITCH
500
722
679
755
display-water-supplies
display-water-supplies
1
1
-1000

BUTTON
461
272
526
305
NIL
test-2\n
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
550
361
635
394
NIL
test-color
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
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

briefcase
true
0
Rectangle -7500403 true true 30 165 270 240
Rectangle -7500403 true true 30 75 270 150
Rectangle -7500403 true true 180 30 195 75
Rectangle -7500403 true true 105 30 120 75
Rectangle -7500403 true true 105 30 180 45
Rectangle -7500403 true true 120 165 195 180
Rectangle -16777216 true false 165 150 180 195
Rectangle -16777216 true false 135 180 180 195
Rectangle -16777216 true false 120 150 135 195

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

clock
true
0
Circle -7500403 true true 30 30 240
Polygon -16777216 true false 150 31 128 75 143 75 143 150 158 150 158 75 173 75
Circle -16777216 true false 135 135 30

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
