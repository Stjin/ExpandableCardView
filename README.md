## Before you try to use this library

This library is forked and modified for the app [Cometin](https://cometin.stjin.host)
I tried making the library as functional for others but there might be some elements you don't like.

# ExpandableCardView

[![](https://jitpack.io/v/Stjinchan/ExpandableCardView.svg)](https://jitpack.io/#Stjinchan/ExpandableCardView)

![Example Gif](demo.gif)

An Android library that lets you create in a simple, fast and hassle-free way a CardView in which you can insert your custom layout and just expand and collapse without even writing a single Java/Kotlin line of code.

This component follows the [Material Design Guidelines](https://material.io/guidelines/).

## Demo

Get it on the [Google Play Store](https://play.google.com/store/apps/details?id=com.alessandrosperotti.expandablecardviewexample).

# Setup

First of all, include the dependency in your app build.gradle:

### Stable
```gradle
implementation 'com.github.Stjinchan:ExpandableCardView:1.1.2'
```

### Beta
```gradle
implementation 'com.github.Stjinchan:ExpandableCardView:1.2.0-beta05'
```

```
This is a beta, introducing a different way of calculating inner_view height as a fix for the [Innerview issue](https://github.com/AleSpero/ExpandableCardView/issues/9)
I am still testing if this works as intended
```

Or get the aar in the [Releases](https://github.com/Stjinchan/ExpandableCardView/releases) section.


## Declaring the view

After you have the Library correctly setup, just declare the ExpandableCardView in your xml:

```xml
<host.stjin.expandablecardview.ExpandableCardView
    android:id="@+id/profile"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:title="Passengers"
    app:icon="@drawable/ic_person"
    app:inner_view="@layout/mycustomview"
    app:expandOnClick="true"
    app:animationDuration="300"
    app:cardStrokeWidth="2dp"
    app:startExpanded="false"/>
```
The only required attributes are `inner_view` and `title`. The other attributes are optional.

You can specify a custom title and icon on the header on the card by setting the attribute ```app:title``` and  ```app:icon``` respectively.

After you created the base xml, just create your custom layout and place it inside your ```layout``` folder.

Now just pass your newly created layout resource to the ```app:inner_view``` attribute. By setting the attribute ```app:expandOnClick="true"``` the card will have a default behaviour (expand/collapse on click); By setting the `animationDuration` attribute you can set a custom animation time, and by setting the `startExpanded` attribute the card will be automatically expanded when inflated.

Done! Now your ExpandableCardView is ready to roll.

## Usage

If you want some basic Expandable Card without any custom behaviour, setting the view in the XML is enough. Otherwise just declare your ExpandableCardView in your Activity/Fragment and you're ready to use its methods.


## Styling
```
app:expandOnClick="true"
app:expandableCardRipple="true"
app:expandableCardRadius="100"
app:expandableCardStrokeWidth="15"
app:expandableCardElevation="70"
app:expandableCardStrokeColor="@color/colorAccent"
app:expandableCardArrowColor="@color/colorAccent"
app:expandableCardTitleColor="@color/colorAccent"
app:expandableCardTitleBold="true"
app:expandableCardTitleSize="30"
app:animationDuration="900"
app:startExpanded="true"
app:showSwitch="true"
```

### Java
```java
ExpandableCardView card = findViewById(R.id.profile);

 //Do stuff here
```
### Kotlin

```kotlin
val card : ExpandableCardView = findViewById(R.id.profile)

 //Do stuff here
```

You can use ```expand()``` and ```collapse()``` to respectively expand and collapse the card, and use ```isExpanded()``` to check if the card is expanded or not.
You can change the title and icon of the card dynamically by using ```setTitle()``` and ```setIcon()``` methods.

You can also set an OnExpandedListener to the card:

### Java
```java
card.setOnExpandedListener(new OnExpandedListener() {
    @Override
    public void onExpandChanged(View v, boolean isExpanded) {
        Toast.makeText(applicationContext, isExpanded ? "Expanded!" : "Collapsed!", Toast.LENGTH_SHORT).show();
    }
});

card.setOnSwitchChangeListener(new OnSwitchChangeListene() {
    @Override
    public void OnSwitchChangeListener(View v, boolean isChecked) {
        Toast.makeText(applicationContext, isChecked ? "Checked!" : "Not checked!", Toast.LENGTH_SHORT).show();
    }
});
```
### Kotlin

```kotlin
card.setOnExpandedListener { view, isExpanded ->
    Toast.makeText(applicationContext, if(isExpanded) "Expanded!" else "Collapsed!", Toast.LENGTH_SHORT).show()
 }

 card.setOnSwitchChangeListener { view, isChecked ->
    Toast.makeText(applicationContext, if(isChecked) "Checked!" else "Not checked!", Toast.LENGTH_SHORT).show()
 }
```

## License

```
  Copyright (c) 2018 Alessandro Sperotti
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
  http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 
```

## Author
Forked project, Made By [Alessandro Sperotti](www.alessandrosperotti.com). 

alessandrosperotti at gmail dot com

If you liked this library, why don't you offer me or Allesandro a coffee (or a beer? :D)

ETH (Alessandro): 0x70b6b9eaCDf6f76ECb92bE3fb81d72B3971BA1Bc

Paypal (Stjinchan (me)): [Donate](https://paypal.me/Stjinchan)
