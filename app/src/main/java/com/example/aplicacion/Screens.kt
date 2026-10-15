package com.example.aplicacion

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.aplicacion.ui.theme.*

@Composable
fun ScreenProducts(navController: NavHostController, servicio: ProductApiService) {
    val allProducts = remember { mutableStateListOf<ProductModel>() }
    var displayedProducts by remember { mutableStateOf(listOf<ProductModel>()) }
    var cargando by remember { mutableStateOf(true) }

    // Estados para Filtro y Búsqueda
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    // Obtenemos las categorías únicas a partir de los productos cargados
    val categorias = remember(allProducts.size) { 
        allProducts.map { it.category }.distinct() 
    }

    // Efecto para Cargar datos 1 sola vez
    LaunchedEffect(Unit) {
        try {
            val respuesta = servicio.getProducts()
            allProducts.clear()
            allProducts.addAll(respuesta.products)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cargando = false
        }
    }

    // Efecto para Filtrar en tiempo real (instantáneo)
    LaunchedEffect(searchQuery, selectedCategory, allProducts.size) {
        var filtrados = allProducts.toList()
        
        if (selectedCategory != null) {
            filtrados = filtrados.filter { it.category == selectedCategory }
        }
        
        if (searchQuery.isNotEmpty()) {
            filtrados = filtrados.filter { 
                it.title.contains(searchQuery, ignoreCase = true) || 
                it.description.contains(searchQuery, ignoreCase = true) 
            }
        }
        
        displayedProducts = filtrados
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Buscador
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Buscar productos...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = PrimaryMarket) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Limpiar")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryMarket,
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )

        // 2. Categorías Deslizables
        if (categorias.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    CategoryChip(
                        text = "Todos",
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                items(categorias) { cat ->
                    CategoryChip(
                        text = cat.replaceFirstChar { it.uppercase() },
                        isSelected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }
        }

        // 3. Grid de Productos o Estado de Carga
        if (cargando) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryMarket)
            }
        } else if (displayedProducts.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No se encontraron productos :(", color = Color.Gray, fontWeight = FontWeight.SemiBold)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayedProducts) { producto ->
                    ProductCard(producto = producto) {
                        navController.navigate("productDetail/${producto.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) PrimaryMarket else Color.White,
        border = BorderStroke(1.dp, if (isSelected) PrimaryMarket else Color.LightGray),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ProductCard(producto: ProductModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp), spotColor = Color.LightGray)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = producto.thumbnail,
                    contentDescription = producto.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                
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
            
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

            Column(modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
            ) {
                Text(
                    text = producto.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    modifier = Modifier.height(36.dp)
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
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
                
                Text(
                    text = "$${producto.price}",
                    fontWeight = FontWeight.Black,
                    color = PriceColor,
                    fontSize = 18.sp
                )
                
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
    val context = LocalContext.current // Contexto para usar en el Toast del botón

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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 320.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(24.dp)
            ) {
                Box(modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = (producto!!.brand ?: producto!!.category).uppercase(),
                    color = PrimaryMarket,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = producto!!.title,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    lineHeight = 28.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
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

                // Botón FUNCIONAL que muestra un mensaje (Toast)
                Button(
                    onClick = { 
                        Toast.makeText(context, "🛒 ${producto!!.title} agregado al carrito", Toast.LENGTH_SHORT).show()
                    },
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
