# I7BachelorProjekt
# 1) Git pull
# 2) Git add < file >
# 3) git commit -m "forklar ændringer"
# 4) git push

# Brug altid git pull inden i begynder at arbejde
 git pull: henter opdateringer og nye filer fra remote reporsitory til dit lokale

git status: viser status på det reporsitory du står i, om der er ændringer i remote eller localt

git add .: tilføjer alle ændringer (vær sikker på at du faktisk vil tilføje alle ændringer)

git commit -m "hvad er nyt": bruges til at forklare hvad du har ændret / tilføjet til næste push

git push: pusher dit nye commit til det remote reporsitory.

git remove 'filnavn': fjerner en mappe eller fil (hvis den skal slettes fra remote repo, skal i pushe den slettede fil)

# MERGE CONFLICTS

I visual studio vil der blive tilføjet "<<<<< HEAD" øverst i merge conflicten
"======" vil blive vist for at skille fra jeres ændringer til dem i prøver at pulle
til sidst vil "<<<<<<<" blive vist for at slutte hvor merge conflicten er.

Tilpas ændringerne ved at slette det over eller under skille linjen (====) og slet så alle pilene, commit og push.

 Hvis i vil overskride og ikke tage jer af jeres egne ændringer men bare pulle og i får en merge conflic, kan i benytte:
 Git fetch (Sætter din kode til at være remote repo koden)
 Git reset --hard (Overskrider jeres egne ændringer med dem i det remote repo
