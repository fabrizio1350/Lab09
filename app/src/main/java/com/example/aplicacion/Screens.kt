package com.example.aplicacion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.aplicacion.ui.theme.*

@Composable
fun ScreenProducts(navController: NavHostController, servicio: ProductApiService) {
    val listaProductos = remember { mutableStateListOf<ProductModel>() }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val respuesta = servicio.getProducts()
            listaProductos.clear()
            listaProductos.addAll(respuesta.products)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cargando = false
        }
    }

    // Fondo general gris clarito
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        
        if (cargando) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listaProductos) { producto ->
                    ProductCard(producto = producto) {
                        navController.navigate("productDetail/${producto.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(producto: ProductModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Sombra suave tipo elevación de iOS/Marketplace
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp), spotColor = Color.LightGray)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            // Contenedor de la imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.White) // Fondo blanco para imágenes transparentes
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = producto.thumbnail,
                    contentDescription = producto.title,
                    contentScale = ContentScale.Fit, // Fit para ver el producto completo sin recortes
                    modifier = Modifier.fillMaxSize()
                )
                
                // Botón de "Favorito" o "Etiqueta" en la esquina superior derecha
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CategoryBadge,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = producto.category.uppercase(),
                        color = TextCategoryBadge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            // Separador sutil
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

            // Contenido de texto (Abajo de la imagen)
            Column(modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
            ) {
                Text(
                    text = producto.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 2, // 2 líneas para títulos largos
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    modifier = Modifier.height(36.dp) // Altura fija para alinear las tarjetas
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Estrellas
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = RatingColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${producto.rating}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Precio Verde estilo oferta
                Text(
                    text = "$${producto.price}",
                    fontWeight = FontWeight.Black,
                    color = PriceColor,
                    fontSize = 18.sp
                )
                
                // Envío gratis (detallito extra para que se vea real)
                Text(
                    text = "Envío gratis",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryMarket,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ScreenProductDetail(navController: NavHostController, servicio: ProductApiService, id: Int) {
    var producto by remember { mutableStateOf<ProductModel?>(null) }

    LaunchedEffect(Unit) {
        try {
            producto = servicio.getProductById(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    if (producto != null) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
        ) {
            // Fondo / Imagen Inmersiva en la parte superior (Fondo blanco)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(Color.White)
                    .padding(32.dp)
            ) {
                AsyncImage(
                    model = producto!!.thumbnail,
                    contentDescription = producto!!.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            // Botón flotante para regresar
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(16.dp)
                    .padding(top = 24.dp)
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
                    .border(1.dp, Color.LightGray.copy(alpha=0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            // Hoja de detalles deslizable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 320.dp) // Deja ver la imagen de fondo
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.background) // Gris muy claro
                    // Sombra arriba
                    .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(24.dp)
            ) {
                
                // Pastilla de arrastre (Visual)
                Box(modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Etiqueta de Marca
                Text(
                    text = (producto!!.brand ?: producto!!.category).uppercase(),
                    color = PrimaryMarket,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Título
                Text(
                    text = producto!!.title,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    lineHeight = 28.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Rating y Vendidos
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = RatingColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${producto!!.rating}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "•", color = Color.LightGray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "+1000 vendidos", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Precio Grande y Verde
                Text(
                    text = "$${producto!!.price}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PriceColor
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Acerca de este artículo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = producto!!.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Botón grande de comprar
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryMarket)
                ) {
                    Icon(imageVector = Icons.Outlined.ShoppingCart, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Agregar al carrito", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryMarket)
        }
    }
}
