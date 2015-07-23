DESCRIPTION
===========
We aim to build an Android application to support both the collection of agronomy data for particular crops and the return of yield predictions based on collected data. 

The yield prediction model will start out as very simple, but can gradually be made more complex over time through server-side calculations that may incorporate remote sensing covariates and allometry. 

We will first focus on maize, which can take 3-4 months to grow.

Input: Maize Yield Component Data
---------------------------------

    1. User identifies growth stage from diagram (see example below).
    2. Number of cobs per unit area (*)
    3. Number of rows per cob
    4. Number of kernels per row (can be counted in R3 growth stage) (**)
    5. Adjustment factor that depends on the growth stage of the corn (R1, R2, R3)
    6. Latitude and Longitude (georeference), auto-calculated
    7. Timestamp, auto-calculated
    8. UUID, if available (***)

Output: Maize Yield Prediction
------------------------------

The yield estimate will grow in complexity over time, as follows:

    1. (Initial Version) Simply return the product of inputs 1, 2, 3, and 4, using a client-side calculation.
    2. Call a server-side function that returns the product of inputs 1, 2, 3, and 4.
    3. Replace the product with more complex allometric equations.
    4. Incorporate soil and rainfall covariates into the prediction.

Design Constraints
------------------
+ Use an OOP-design that will allow us to easily switch the crop of interest in the future, from maize to bananas or cassava trees.
+ Try using Parse for the back-end.